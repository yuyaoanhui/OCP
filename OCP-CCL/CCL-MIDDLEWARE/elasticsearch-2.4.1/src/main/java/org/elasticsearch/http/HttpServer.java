/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.http;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;

import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.breaker.CircuitBreaker;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.component.AbstractLifecycleComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.io.FileSystemUtils;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.indices.breaker.CircuitBreakerService;
import org.elasticsearch.node.service.NodeService;
import org.elasticsearch.rest.*;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestResponse;
import org.elasticsearch.rest.RestStatus;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.elasticsearch.rest.RestStatus.*;

/**
 *
 */
public class HttpServer extends AbstractLifecycleComponent<HttpServer> {

    private final Environment environment;

    private final HttpServerTransport transport;

    private final RestController restController;

    private final NodeService nodeService;

    private final boolean disableSites;

    private final PluginSiteFilter pluginSiteFilter = new PluginSiteFilter();

    private final CircuitBreakerService circuitBreakerService;

    @Inject
    public HttpServer(Settings settings, Environment environment, HttpServerTransport transport, RestController restController, NodeService nodeService,
                      CircuitBreakerService circuitBreakerService) {
        super(settings);
        this.environment = environment;
        this.transport = transport;
        this.restController = restController;
        this.nodeService = nodeService;
        this.circuitBreakerService = circuitBreakerService;
        nodeService.setHttpServer(this);

        this.disableSites = this.settings.getAsBoolean("http.disable_sites", false);

        transport.httpServerAdapter(new Dispatcher(this));
    }

    static class Dispatcher implements HttpServerAdapter {

        private final HttpServer server;

        Dispatcher(HttpServer server) {
            this.server = server;
        }

        @Override
        public void dispatchRequest(RestRequest request, RestChannel channel) {
            server.internalDispatchRequest(request, channel);
        }
    }

    @Override
    protected void doStart() {
        transport.start();
        if (logger.isInfoEnabled()) {
            logger.info("{}", transport.boundAddress());
        }
        nodeService.putAttribute("http_address", transport.boundAddress().publishAddress().toString());
    }

    @Override
    protected void doStop() {
        nodeService.removeAttribute("http_address");
        transport.stop();
    }

    @Override
    protected void doClose() {
        transport.close();
    }

    public HttpInfo info() {
        return transport.info();
    }

    public HttpStats stats() {
        return transport.stats();
    }

    public void internalDispatchRequest(final RestRequest request, final RestChannel channel) {
        String rawPath = request.rawPath();
        if (rawPath.startsWith("/_plugin/")) {
            RestFilterChain filterChain = restController.filterChain(pluginSiteFilter);
            filterChain.continueProcessing(request, channel);
            return;
        } else if (rawPath.equals("/favicon.ico")) {
            handleFavicon(request, channel);
            return;
        }
        RestChannel responseChannel = channel;
        try {
            int contentLength = request.content().length();
            if (restController.canTripCircuitBreaker(request)) {
                inFlightRequestsBreaker(circuitBreakerService).addEstimateBytesAndMaybeBreak(contentLength, "<http_request>");
            } else {
                inFlightRequestsBreaker(circuitBreakerService).addWithoutBreaking(contentLength);
            }
            // iff we could reserve bytes for the request we need to send the response also over this channel
            responseChannel = new ResourceHandlingHttpChannel(channel, circuitBreakerService);
            restController.dispatchRequest(request, responseChannel);
        } catch (Throwable t) {
            restController.sendErrorResponse(request, responseChannel, t);
        }
    }


    class PluginSiteFilter extends RestFilter {

        @Override
        public void process(RestRequest request, RestChannel channel, RestFilterChain filterChain) throws IOException {
            handlePluginSite(request, channel);
        }
    }

    void handleFavicon(RestRequest request, RestChannel channel) {
        if (request.method() == RestRequest.Method.GET) {
            try {
                try (InputStream stream = getClass().getResourceAsStream("/config/favicon.ico")) {
                    byte[] content = ByteStreams.toByteArray(stream);
                    BytesRestResponse restResponse = new BytesRestResponse(RestStatus.OK, "image/x-icon", content);
                    channel.sendResponse(restResponse);
                }
            } catch (IOException e) {
                channel.sendResponse(new BytesRestResponse(INTERNAL_SERVER_ERROR));
            }
        } else {
            channel.sendResponse(new BytesRestResponse(FORBIDDEN));
        }
    }

    void handlePluginSite(RestRequest request, RestChannel channel) throws IOException {
        if (disableSites) {
            channel.sendResponse(new BytesRestResponse(FORBIDDEN));
            return;
        }
        if (request.method() == RestRequest.Method.OPTIONS) {
            // when we have OPTIONS request, simply send OK by default (with the Access Control Origin header which gets automatically added)
            channel.sendResponse(new BytesRestResponse(OK));
            return;
        }
        if (request.method() != RestRequest.Method.GET) {
            channel.sendResponse(new BytesRestResponse(FORBIDDEN));
            return;
        }
        // TODO for a "/_plugin" endpoint, we should have a page that lists all the plugins?

        String path = request.rawPath().substring("/_plugin/".length());
        int i1 = path.indexOf('/');
        String pluginName;
        String sitePath;
        if (i1 == -1) {
            pluginName = path;
            sitePath = null;
            // If a trailing / is missing, we redirect to the right page #2654
            String redirectUrl = request.rawPath() + "/";
            BytesRestResponse restResponse = new BytesRestResponse(RestStatus.MOVED_PERMANENTLY, "text/html", "<head><meta http-equiv=\"refresh\" content=\"0; URL=" + redirectUrl + "\"></head>");
            restResponse.addHeader("Location", redirectUrl);
            channel.sendResponse(restResponse);
            return;
        } else {
            pluginName = path.substring(0, i1);
            sitePath = path.substring(i1 + 1);
        }

        // we default to index.html, or what the plugin provides (as a unix-style path)
        // this is a relative path under _site configured by the plugin.
        if (sitePath.length() == 0) {
            sitePath = "index.html";
        } else {
            // remove extraneous leading slashes, its not an absolute path.
            while (sitePath.length() > 0 && sitePath.charAt(0) == '/') {
                sitePath = sitePath.substring(1);
            }
        }
        final Path siteFile = environment.pluginsFile().resolve(pluginName).resolve("_site");

        final String separator = siteFile.getFileSystem().getSeparator();
        // Convert file separators.
        sitePath = sitePath.replace("/", separator);

        Path file = siteFile.resolve(sitePath);

        // return not found instead of forbidden to prevent malicious requests to find out if files exist or dont exist
        if (!Files.exists(file) || FileSystemUtils.isHidden(file) || !file.toAbsolutePath().normalize().startsWith(siteFile.toAbsolutePath().normalize())) {
            channel.sendResponse(new BytesRestResponse(NOT_FOUND));
            return;
        }

        BasicFileAttributes attributes = Files.readAttributes(file, BasicFileAttributes.class);
        if (!attributes.isRegularFile()) {
            // If it's not a dir, we send a 403
            if (!attributes.isDirectory()) {
                channel.sendResponse(new BytesRestResponse(FORBIDDEN));
                return;
            }
            // We don't serve dir but if index.html exists in dir we should serve it
            file = file.resolve("index.html");
            if (!Files.exists(file) || FileSystemUtils.isHidden(file) || !Files.isRegularFile(file)) {
                channel.sendResponse(new BytesRestResponse(FORBIDDEN));
                return;
            }
        }

        try {
            byte[] data = Files.readAllBytes(file);
            channel.sendResponse(new BytesRestResponse(OK, guessMimeType(sitePath), data));
        } catch (IOException e) {
            channel.sendResponse(new BytesRestResponse(INTERNAL_SERVER_ERROR));
        }
    }


    // TODO: Don't respond with a mime type that violates the request's Accept header
    private String guessMimeType(String path) {
        int lastDot = path.lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        String extension = path.substring(lastDot + 1).toLowerCase(Locale.ROOT);
        String mimeType = DEFAULT_MIME_TYPES.get(extension);
        if (mimeType == null) {
            return "";
        }
        return mimeType;
    }

    static {
        // This is not an exhaustive list, just the most common types. Call registerMimeType() to add more.
        Map<String, String> mimeTypes = new HashMap<>();
        mimeTypes.put("txt", "text/plain");
        mimeTypes.put("css", "text/css");
        mimeTypes.put("csv", "text/csv");
        mimeTypes.put("htm", "text/html");
        mimeTypes.put("html", "text/html");
        mimeTypes.put("xml", "text/xml");
        mimeTypes.put("js", "text/javascript"); // Technically it should be application/javascript (RFC 4329), but IE8 struggles with that
        mimeTypes.put("xhtml", "application/xhtml+xml");
        mimeTypes.put("json", "application/json");
        mimeTypes.put("pdf", "application/pdf");
        mimeTypes.put("zip", "application/zip");
        mimeTypes.put("tar", "application/x-tar");
        mimeTypes.put("gif", "image/gif");
        mimeTypes.put("jpeg", "image/jpeg");
        mimeTypes.put("jpg", "image/jpeg");
        mimeTypes.put("tiff", "image/tiff");
        mimeTypes.put("tif", "image/tiff");
        mimeTypes.put("png", "image/png");
        mimeTypes.put("svg", "image/svg+xml");
        mimeTypes.put("ico", "image/vnd.microsoft.icon");
        mimeTypes.put("mp3", "audio/mpeg");
        DEFAULT_MIME_TYPES = ImmutableMap.copyOf(mimeTypes);
    }

    public static final Map<String, String> DEFAULT_MIME_TYPES;

    private static final class ResourceHandlingHttpChannel implements RestChannel {
        private final RestChannel delegate;
        private final CircuitBreakerService circuitBreakerService;
        private final AtomicBoolean closed = new AtomicBoolean();

        public ResourceHandlingHttpChannel(RestChannel delegate, CircuitBreakerService circuitBreakerService) {
            this.delegate = delegate;
            this.circuitBreakerService = circuitBreakerService;
        }

        @Override
        public XContentBuilder newBuilder() throws IOException {
            return delegate.newBuilder();
        }

        @Override
        public XContentBuilder newErrorBuilder() throws IOException {
            return delegate.newErrorBuilder();
        }

        @Override
        public XContentBuilder newBuilder(@Nullable BytesReference autoDetectSource, boolean useFiltering) throws IOException {
            return delegate.newBuilder(autoDetectSource, useFiltering);
        }

        @Override
        public BytesStreamOutput bytesOutput() {
            return delegate.bytesOutput();
        }

        @Override
        public RestRequest request() {
            return delegate.request();
        }

        @Override
        public boolean detailedErrorsEnabled() {
            return delegate.detailedErrorsEnabled();
        }

        @Override
        public void sendResponse(RestResponse response) {
            close();
            delegate.sendResponse(response);
        }

        private void close() {
            // attempt to close once atomically
            if (closed.compareAndSet(false, true) == false) {
                throw new IllegalStateException("Channel is already closed");
            }
            inFlightRequestsBreaker(circuitBreakerService).addWithoutBreaking(-request().content().length());
        }

    }

    private static CircuitBreaker inFlightRequestsBreaker(CircuitBreakerService circuitBreakerService) {
        // We always obtain a fresh breaker to reflect changes to the breaker configuration.
        return circuitBreakerService.getBreaker(CircuitBreaker.IN_FLIGHT_REQUESTS);
    }
}
