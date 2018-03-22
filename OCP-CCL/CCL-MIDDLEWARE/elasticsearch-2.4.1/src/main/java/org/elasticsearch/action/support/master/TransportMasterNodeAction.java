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

package org.elasticsearch.action.support.master;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionListenerResponseHandler;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.ActionRunnable;
import org.elasticsearch.action.support.ActionFilters;
import org.elasticsearch.action.support.HandledTransportAction;
import org.elasticsearch.action.support.ThreadedActionListener;
import org.elasticsearch.cluster.ClusterChangedEvent;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.ClusterStateObserver;
import org.elasticsearch.cluster.NotMasterException;
import org.elasticsearch.cluster.block.ClusterBlockException;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.discovery.MasterNotDiscoveredException;
import org.elasticsearch.node.NodeClosedException;
import org.elasticsearch.tasks.Task;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.ConnectTransportException;
import org.elasticsearch.transport.TransportException;
import org.elasticsearch.transport.TransportService;

/**
 * A base class for operations that needs to be performed on the master node.
 */
public abstract class TransportMasterNodeAction<Request extends MasterNodeRequest, Response extends ActionResponse> extends HandledTransportAction<Request, Response> {
    private static final ClusterStateObserver.ChangePredicate masterNodeChangedPredicate = new ClusterStateObserver.ChangePredicate() {
        @Override
        public boolean apply(ClusterState previousState, ClusterState.ClusterStateStatus previousStatus,
                             ClusterState newState, ClusterState.ClusterStateStatus newStatus) {
            // The condition !newState.nodes().masterNodeId().equals(previousState.nodes().masterNodeId()) is not sufficient as the same master node might get reelected after a disruption.
            return newState.nodes().masterNodeId() != null && newState != previousState;
        }

        @Override
        public boolean apply(ClusterChangedEvent event) {
            return event.nodesDelta().masterNodeChanged();
        }
    };

    protected final TransportService transportService;
    protected final ClusterService clusterService;

    final String executor;

    protected TransportMasterNodeAction(Settings settings, String actionName, TransportService transportService,
                                        ClusterService clusterService, ThreadPool threadPool, ActionFilters actionFilters,
                                        IndexNameExpressionResolver indexNameExpressionResolver, Class<Request> request) {
        super(settings, actionName, threadPool, transportService, actionFilters, indexNameExpressionResolver, request);
        this.transportService = transportService;
        this.clusterService = clusterService;
        this.executor = executor();
    }

    protected TransportMasterNodeAction(Settings settings, String actionName, boolean canTripCircuitBreaker,TransportService transportService,
                                        ClusterService clusterService, ThreadPool threadPool, ActionFilters actionFilters,
                                        IndexNameExpressionResolver indexNameExpressionResolver, Class<Request> request) {
        super(settings, actionName, canTripCircuitBreaker, threadPool, transportService, actionFilters, indexNameExpressionResolver, request);
        this.transportService = transportService;
        this.clusterService = clusterService;
        this.executor = executor();
    }

    protected abstract String executor();

    protected abstract Response newResponse();

    protected abstract void masterOperation(Request request, ClusterState state, ActionListener<Response> listener) throws Exception;

    /**
     * Override this operation if access to the task parameter is needed
     */
    protected void masterOperation(Task task, Request request, ClusterState state, ActionListener<Response> listener) throws Exception {
        masterOperation(request, state, listener);
    }

    protected boolean localExecute(Request request) {
        return false;
    }

    protected abstract ClusterBlockException checkBlock(Request request, ClusterState state);

    @Override
    protected final void doExecute(final Request request, ActionListener<Response> listener) {
        logger.warn("attempt to execute a master node operation without task");
        throw new UnsupportedOperationException("task parameter is required for this operation");
    }

    @Override
    protected void doExecute(Task task, final Request request, ActionListener<Response> listener) {
        new AsyncSingleAction(task, request, listener).start();
    }

    class AsyncSingleAction {

        private final ActionListener<Response> listener;
        private final Request request;
        private volatile ClusterStateObserver observer;
        private final Task task;

        private final ClusterStateObserver.ChangePredicate retryableOrNoBlockPredicate = new ClusterStateObserver.ValidationPredicate() {
            @Override
            protected boolean validate(ClusterState newState) {
                ClusterBlockException blockException = checkBlock(request, newState);
                return (blockException == null || !blockException.retryable());
            }
        };

        AsyncSingleAction(Task task, Request request, ActionListener<Response> listener) {
            this.task = task;
            this.request = request;
            if (task != null) {
                request.setParentTask(clusterService.localNode().getId(), task.getId());
            }
            // TODO do we really need to wrap it in a listener? the handlers should be cheap
            if ((listener instanceof ThreadedActionListener) == false) {
                listener = new ThreadedActionListener<>(logger, threadPool, ThreadPool.Names.LISTENER, listener);
            }
            this.listener = listener;
        }

        public void start() {
            this.observer = new ClusterStateObserver(clusterService, request.masterNodeTimeout(), logger);
            doStart();
        }

        protected void doStart() {
            final ClusterState clusterState = observer.observedState();
            final DiscoveryNodes nodes = clusterState.nodes();
            if (nodes.localNodeMaster() || localExecute(request)) {
                // check for block, if blocked, retry, else, execute locally
                final ClusterBlockException blockException = checkBlock(request, clusterState);
                if (blockException != null) {
                    if (!blockException.retryable()) {
                        listener.onFailure(blockException);
                    } else {
                        logger.trace("can't execute due to a cluster block, retrying", blockException);
                        retry(blockException, retryableOrNoBlockPredicate);
                    }
                } else {
                    final ActionListener<Response> delegate = new ActionListener<Response>() {
                        @Override
                        public void onResponse(Response response) {
                            listener.onResponse(response);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            if (t instanceof NotMasterException) {
                                logger.debug("master could not publish cluster state or stepped down before publishing action [{}], scheduling a retry", t, actionName);
                                retry(t, masterNodeChangedPredicate);
                            } else {
                                listener.onFailure(t);
                            }
                        }
                    };
                    taskManager.registerChildTask(task, nodes.getLocalNodeId());
                    threadPool.executor(executor).execute(new ActionRunnable(delegate) {
                        @Override
                        protected void doRun() throws Exception {
                            masterOperation(task, request, clusterService.state(), delegate);
                        }
                    });
                }
            } else {
                if (nodes.masterNode() == null) {
                    logger.debug("no known master node, scheduling a retry");
                    retry(null, masterNodeChangedPredicate);
                } else {
                    taskManager.registerChildTask(task, nodes.masterNode().getId());
                    transportService.sendRequest(nodes.masterNode(), actionName, request, new ActionListenerResponseHandler<Response>(listener) {
                        @Override
                        public Response newInstance() {
                            return newResponse();
                        }

                        @Override
                        public void handleException(final TransportException exp) {
                            Throwable cause = exp.unwrapCause();
                            if (cause instanceof ConnectTransportException) {
                                // we want to retry here a bit to see if a new master is elected
                                logger.debug("connection exception while trying to forward request with action name [{}] to master node [{}], scheduling a retry. Error: [{}]",
                                        actionName, nodes.masterNode(), exp.getDetailedMessage());
                                retry(cause, masterNodeChangedPredicate);
                            } else {
                                listener.onFailure(exp);
                            }
                        }
                    });
                }
            }
        }

        private void retry(final Throwable failure, final ClusterStateObserver.ChangePredicate changePredicate) {
            observer.waitForNextChange(
                new ClusterStateObserver.Listener() {
                    @Override
                    public void onNewClusterState(ClusterState state) {
                        doStart();
                    }

                    @Override
                    public void onClusterServiceClose() {
                        listener.onFailure(new NodeClosedException(clusterService.localNode()));
                    }

                    @Override
                    public void onTimeout(TimeValue timeout) {
                        logger.debug("timed out while retrying [{}] after failure (timeout [{}])", failure, actionName, timeout);
                        listener.onFailure(new MasterNotDiscoveredException(failure));
                    }
                }, changePredicate
            );
        }
    }
}
