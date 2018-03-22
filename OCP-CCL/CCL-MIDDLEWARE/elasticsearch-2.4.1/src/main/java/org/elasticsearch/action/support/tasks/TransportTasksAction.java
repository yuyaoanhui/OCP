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

package org.elasticsearch.action.support.tasks;

import com.google.common.base.Supplier;
import org.elasticsearch.ResourceNotFoundException;
import org.elasticsearch.Version;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.FailedNodeException;
import org.elasticsearch.action.NoSuchNodeException;
import org.elasticsearch.action.TaskOperationFailure;
import org.elasticsearch.action.support.ActionFilters;
import org.elasticsearch.action.support.ChildTaskRequest;
import org.elasticsearch.action.support.HandledTransportAction;
import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.io.stream.Writeable;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.tasks.Task;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.BaseTransportResponseHandler;
import org.elasticsearch.transport.NodeShouldNotConnectException;
import org.elasticsearch.transport.TransportChannel;
import org.elasticsearch.transport.TransportException;
import org.elasticsearch.transport.TransportRequestHandler;
import org.elasticsearch.transport.TransportRequestOptions;
import org.elasticsearch.transport.TransportResponse;
import org.elasticsearch.transport.TransportService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import org.elasticsearch.common.util.Consumer;

/**
 * The base class for transport actions that are interacting with currently running tasks.
 */
public abstract class TransportTasksAction<
    OperationTask extends Task,
    TasksRequest extends BaseTasksRequest<TasksRequest>,
    TasksResponse extends BaseTasksResponse,
    TaskResponse extends Writeable<TaskResponse>
    > extends HandledTransportAction<TasksRequest, TasksResponse> {

    protected final ClusterName clusterName;
    protected final ClusterService clusterService;
    protected final TransportService transportService;
    protected final Callable<TasksRequest> requestFactory;

    protected final String transportNodeAction;

    protected TransportTasksAction(Settings settings, String actionName, ClusterName clusterName, ThreadPool threadPool,
                                   ClusterService clusterService, TransportService transportService, ActionFilters actionFilters,
                                   IndexNameExpressionResolver indexNameExpressionResolver, Callable<TasksRequest> requestFactory,
                                   String nodeExecutor) {
        super(settings, actionName, true, threadPool, transportService, actionFilters, indexNameExpressionResolver, requestFactory);
        this.clusterName = clusterName;
        this.clusterService = clusterService;
        this.transportService = transportService;
        this.transportNodeAction = actionName + "[n]";
        this.requestFactory = requestFactory;

        transportService.registerRequestHandler(transportNodeAction, new Callable<NodeTaskRequest>() {
            @Override
            public NodeTaskRequest call() throws Exception {
                return new NodeTaskRequest();
            }
        }, nodeExecutor, new NodeTransportHandler());
    }

    @Override
    protected final void doExecute(TasksRequest request, ActionListener<TasksResponse> listener) {
        logger.warn("attempt to execute a transport tasks operation without a task");
        throw new UnsupportedOperationException("task parameter is required for this operation");
    }

    @Override
    protected void doExecute(Task task, TasksRequest request, ActionListener<TasksResponse> listener) {
        new AsyncAction(task, request, listener).start();
    }

    private NodeTasksResponse nodeOperation(NodeTaskRequest nodeTaskRequest) {
        final TasksRequest request = nodeTaskRequest.tasksRequest;
        final List<TaskResponse> results = new ArrayList<>();
        final List<TaskOperationFailure> exceptions = new ArrayList<>();
        processTasks(request, new Consumer<OperationTask>() {
                @Override
                public void accept(OperationTask task) {
                    try {
                        TaskResponse response = taskOperation(request, task);
                        if (response != null) {
                            results.add(response);
                        }
                    } catch (Exception ex) {
                        exceptions.add(new TaskOperationFailure(clusterService.localNode().id(), task.getId(), ex));
                    }
                }
            });
        return new NodeTasksResponse(clusterService.localNode().id(), results, exceptions);
    }

    protected String[] filterNodeIds(DiscoveryNodes nodes, String[] nodesIds) {
        // Filter out all old nodes that don't support task management API
        List<String> supportedNodes = new ArrayList<>(nodesIds.length);
        for (String nodeId : nodesIds) {
            DiscoveryNode node = nodes.get(nodeId);
            if(node != null && node.version().onOrAfter(Version.V_2_3_0)) {
                supportedNodes.add(nodeId);
            }
        }
        return supportedNodes.toArray(new String[supportedNodes.size()]);
    }

    protected String[] resolveNodes(TasksRequest request, ClusterState clusterState) {
        if (request.getTaskId().isSet()) {
            return new String[]{request.getTaskId().getNodeId()};
        } else {
            return clusterState.nodes().resolveNodesIds(request.getNodesIds());
        }
    }

    protected void processTasks(TasksRequest request, Consumer<OperationTask> operation) {
        if (request.getTaskId().isSet()) {
            // we are only checking one task, we can optimize it
            Task task = taskManager.getTask(request.getTaskId().getId());
            if (task != null) {
                if (request.match(task)) {
                    operation.accept((OperationTask) task);
                } else {
                    throw new ResourceNotFoundException("task [{}] doesn't support this operation", request.getTaskId());
                }
            } else {
                throw new ResourceNotFoundException("task [{}] is missing", request.getTaskId());
            }
        } else {
            for (Task task : taskManager.getTasks().values()) {
                if (request.match(task)) {
                    operation.accept((OperationTask) task);
                }
            }
        }
    }

    protected abstract TasksResponse newResponse(TasksRequest request, List<TaskResponse> tasks, List<TaskOperationFailure>
        taskOperationFailures, List<FailedNodeException> failedNodeExceptions);

    @SuppressWarnings("unchecked")
    protected TasksResponse newResponse(TasksRequest request, AtomicReferenceArray responses) {
        List<TaskResponse> tasks = new ArrayList<>();
        List<FailedNodeException> failedNodeExceptions = new ArrayList<>();
        List<TaskOperationFailure> taskOperationFailures = new ArrayList<>();
        for (int i = 0; i < responses.length(); i++) {
            Object response = responses.get(i);
            if (response instanceof FailedNodeException) {
                failedNodeExceptions.add((FailedNodeException) response);
            } else {
                NodeTasksResponse tasksResponse = (NodeTasksResponse) response;
                if (tasksResponse.results != null) {
                    tasks.addAll(tasksResponse.results);
                }
                if (tasksResponse.exceptions != null) {
                    taskOperationFailures.addAll(tasksResponse.exceptions);
                }
            }
        }
        return newResponse(request, tasks, taskOperationFailures, failedNodeExceptions);
    }

    protected abstract TaskResponse readTaskResponse(StreamInput in) throws IOException;

    protected abstract TaskResponse taskOperation(TasksRequest request, OperationTask task);

    protected boolean transportCompress() {
        return false;
    }

    protected abstract boolean accumulateExceptions();

    private class AsyncAction {

        private final TasksRequest request;
        private final String[] nodesIds;
        private final DiscoveryNode[] nodes;
        private final ActionListener<TasksResponse> listener;
        private final AtomicReferenceArray<Object> responses;
        private final AtomicInteger counter = new AtomicInteger();
        private final Task task;

        private AsyncAction(Task task, TasksRequest request, ActionListener<TasksResponse> listener) {
            this.task = task;
            this.request = request;
            this.listener = listener;
            ClusterState clusterState = clusterService.state();
            String[] nodesIds = resolveNodes(request, clusterState);
            this.nodesIds = filterNodeIds(clusterState.nodes(), nodesIds);
            ImmutableOpenMap<String, DiscoveryNode> nodes = clusterState.nodes().nodes();
            this.nodes = new DiscoveryNode[nodesIds.length];
            for (int i = 0; i < this.nodesIds.length; i++) {
                this.nodes[i] = nodes.get(this.nodesIds[i]);
            }
            this.responses = new AtomicReferenceArray<>(this.nodesIds.length);
        }

        private void start() {
            if (nodesIds.length == 0) {
                // nothing to do
                try {
                    listener.onResponse(newResponse(request, responses));
                } catch (Throwable t) {
                    logger.debug("failed to generate empty response", t);
                    listener.onFailure(t);
                }
            } else {
                TransportRequestOptions.Builder builder = TransportRequestOptions.builder();
                if (request.getTimeout() != null) {
                    builder.withTimeout(request.getTimeout());
                }
                builder.withCompress(transportCompress());
                for (int i = 0; i < nodesIds.length; i++) {
                    final String nodeId = nodesIds[i];
                    final int idx = i;
                    final DiscoveryNode node = nodes[i];
                    try {
                        if (node == null) {
                            onFailure(idx, nodeId, new NoSuchNodeException(nodeId));
                        } else if (!clusterService.localNode().shouldConnectTo(node) && !clusterService.localNode().equals(node)) {
                            // the check "!clusterService.localNode().equals(node)" is to maintain backward comp. where before
                            // we allowed to connect from "local" client node to itself, certain tests rely on it, if we remove it, we
                            // need to fix
                            // those (and they randomize the client node usage, so tricky to find when)
                            onFailure(idx, nodeId, new NodeShouldNotConnectException(clusterService.localNode(), node));
                        } else {
                            NodeTaskRequest nodeRequest = new NodeTaskRequest(request);
                            nodeRequest.setParentTask(clusterService.localNode().id(), task.getId());
                            taskManager.registerChildTask(task, node.getId());
                            transportService.sendRequest(node, transportNodeAction, nodeRequest, builder.build(),
                                new BaseTransportResponseHandler<NodeTasksResponse>() {
                                    @Override
                                    public NodeTasksResponse newInstance() {
                                        return new NodeTasksResponse();
                                    }

                                    @Override
                                    public void handleResponse(NodeTasksResponse response) {
                                        onOperation(idx, response);
                                    }

                                    @Override
                                    public void handleException(TransportException exp) {
                                        onFailure(idx, node.id(), exp);
                                    }

                                    @Override
                                    public String executor() {
                                        return ThreadPool.Names.SAME;
                                    }
                                });
                        }
                    } catch (Throwable t) {
                        onFailure(idx, nodeId, t);
                    }
                }
            }
        }

        private void onOperation(int idx, NodeTasksResponse nodeResponse) {
            responses.set(idx, nodeResponse);
            if (counter.incrementAndGet() == responses.length()) {
                finishHim();
            }
        }

        private void onFailure(int idx, String nodeId, Throwable t) {
            if (logger.isDebugEnabled() && !(t instanceof NodeShouldNotConnectException)) {
                logger.debug("failed to execute on node [{}]", t, nodeId);
            }
            if (accumulateExceptions()) {
                responses.set(idx, new FailedNodeException(nodeId, "Failed node [" + nodeId + "]", t));
            }
            if (counter.incrementAndGet() == responses.length()) {
                finishHim();
            }
        }

        private void finishHim() {
            TasksResponse finalResponse;
            try {
                finalResponse = newResponse(request, responses);
            } catch (Throwable t) {
                logger.debug("failed to combine responses from nodes", t);
                listener.onFailure(t);
                return;
            }
            listener.onResponse(finalResponse);
        }
    }

    class NodeTransportHandler extends TransportRequestHandler<NodeTaskRequest> {

        @Override
        public void messageReceived(final NodeTaskRequest request, final TransportChannel channel) throws Exception {
            channel.sendResponse(nodeOperation(request));
        }
    }


    private class NodeTaskRequest extends ChildTaskRequest {
        private TasksRequest tasksRequest;

        protected NodeTaskRequest() {
            super();
        }

        protected NodeTaskRequest(TasksRequest tasksRequest) {
            super(tasksRequest);
            this.tasksRequest = tasksRequest;
        }

        @Override
        public void readFrom(StreamInput in) throws IOException {
            super.readFrom(in);
            try {
                tasksRequest = requestFactory.call();
            } catch (Exception ex) {
                throw new IOException("cannot create task request", ex);
            }

            tasksRequest.readFrom(in);
        }

        @Override
        public void writeTo(StreamOutput out) throws IOException {
            super.writeTo(out);
            tasksRequest.writeTo(out);
        }
    }

    private class NodeTasksResponse extends TransportResponse {
        protected String nodeId;
        protected List<TaskOperationFailure> exceptions;
        protected List<TaskResponse> results;

        public NodeTasksResponse() {
        }

        public NodeTasksResponse(String nodeId,
                                 List<TaskResponse> results,
                                 List<TaskOperationFailure> exceptions) {
            this.nodeId = nodeId;
            this.results = results;
            this.exceptions = exceptions;
        }

        public String getNodeId() {
            return nodeId;
        }

        public List<TaskOperationFailure> getExceptions() {
            return exceptions;
        }

        @Override
        public void readFrom(StreamInput in) throws IOException {
            super.readFrom(in);
            nodeId = in.readString();
            int resultsSize = in.readVInt();
            results = new ArrayList<>(resultsSize);
            for (; resultsSize > 0; resultsSize--) {
                final TaskResponse result = in.readBoolean() ? readTaskResponse(in) : null;
                results.add(result);
            }
            if (in.readBoolean()) {
                int taskFailures = in.readVInt();
                exceptions = new ArrayList<>(taskFailures);
                for (int i = 0; i < taskFailures; i++) {
                    exceptions.add(new TaskOperationFailure(in));
                }
            } else {
                exceptions = null;
            }
        }

        @Override
        public void writeTo(StreamOutput out) throws IOException {
            super.writeTo(out);
            out.writeString(nodeId);
            out.writeVInt(results.size());
            for (TaskResponse result : results) {
                if (result != null) {
                    out.writeBoolean(true);
                    result.writeTo(out);
                } else {
                    out.writeBoolean(false);
                }
            }
            out.writeBoolean(exceptions != null);
            if (exceptions != null) {
                int taskFailures = exceptions.size();
                out.writeVInt(taskFailures);
                for (TaskOperationFailure exception : exceptions) {
                    exception.writeTo(out);
                }
            }
        }
    }
}
