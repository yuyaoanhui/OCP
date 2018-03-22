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

package org.elasticsearch.action.support.replication;

import org.elasticsearch.Version;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.IndicesRequest;
import org.elasticsearch.action.WriteConsistencyLevel;
import org.elasticsearch.action.support.ChildTaskActionRequest;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.shard.ShardId;
import org.elasticsearch.tasks.Task;
import org.elasticsearch.tasks.TaskId;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.elasticsearch.action.ValidateActions.addValidationError;

/**
 *
 */
public class ReplicationRequest<T extends ReplicationRequest<T>> extends ChildTaskActionRequest<T> implements IndicesRequest {

    public static final TimeValue DEFAULT_TIMEOUT = new TimeValue(1, TimeUnit.MINUTES);

    /**
     * Target shard the request should execute on. In case of index and delete requests,
     * shard id gets resolved by the transport action before performing request operation
     * and at request creation time for shard-level bulk, refresh and flush requests.
     */
    protected ShardId shardId;

    protected TimeValue timeout = DEFAULT_TIMEOUT;
    protected String index;

    private WriteConsistencyLevel consistencyLevel = WriteConsistencyLevel.DEFAULT;
    private volatile boolean canHaveDuplicates = false;

    private long routedBasedOnClusterVersion = 0;

    public ReplicationRequest() {

    }

    /**
     * Creates a new request that inherits headers and context from the request provided as argument.
     */
    public ReplicationRequest(ActionRequest request) {
        super(request);
    }

    /**
     * Creates a new request with resolved shard id
     */
    public ReplicationRequest(ActionRequest request, ShardId shardId) {
        super(request);
        this.index = shardId.getIndex();
        this.shardId = shardId;
    }

    /**
     * Copy constructor that creates a new request that is a copy of the one provided as an argument.
     */
    protected ReplicationRequest(T request) {
        this(request, request);
    }

    /**
     * Copy constructor that creates a new request that is a copy of the one provided as an argument.
     * The new request will inherit though headers and context from the original request that caused it.
     */
    protected ReplicationRequest(T request, ActionRequest originalRequest) {
        super(originalRequest);
        this.timeout = request.timeout();
        this.index = request.index();
        this.consistencyLevel = request.consistencyLevel();
    }

    void setCanHaveDuplicates() {
        this.canHaveDuplicates = true;
    }

    /**
     * Is this request can potentially be dup on a single shard.
     */
    public boolean canHaveDuplicates() {
        return canHaveDuplicates;
    }

    /**
     * A timeout to wait if the index operation can't be performed immediately. Defaults to <tt>1m</tt>.
     */
    @SuppressWarnings("unchecked")
    public final T timeout(TimeValue timeout) {
        this.timeout = timeout;
        return (T) this;
    }

    /**
     * A timeout to wait if the index operation can't be performed immediately. Defaults to <tt>1m</tt>.
     */
    public final T timeout(String timeout) {
        return timeout(TimeValue.parseTimeValue(timeout, null, getClass().getSimpleName() + ".timeout"));
    }

    public TimeValue timeout() {
        return timeout;
    }

    public String index() {
        return this.index;
    }

    @SuppressWarnings("unchecked")
    public final T index(String index) {
        this.index = index;
        return (T) this;
    }

    @Override
    public String[] indices() {
        return new String[]{index};
    }

    @Override
    public IndicesOptions indicesOptions() {
        return IndicesOptions.strictSingleIndexNoExpandForbidClosed();
    }

    public WriteConsistencyLevel consistencyLevel() {
        return this.consistencyLevel;
    }

    /**
     * @return the shardId of the shard where this operation should be executed on.
     * can be null if the shardID has not yet been resolved
     */
    public
    @Nullable
    ShardId shardId() {
        return shardId;
    }

    /**
     * Sets the consistency level of write. Defaults to {@link org.elasticsearch.action.WriteConsistencyLevel#DEFAULT}
     */
    @SuppressWarnings("unchecked")
    public final T consistencyLevel(WriteConsistencyLevel consistencyLevel) {
        this.consistencyLevel = consistencyLevel;
        return (T) this;
    }

    /**
     * Sets the minimum version of the cluster state that is required on the next node before we redirect to another primary.
     * Used to prevent redirect loops, see also {@link TransportReplicationAction.ReroutePhase#doRun()}
     */
    @SuppressWarnings("unchecked")
    T routedBasedOnClusterVersion(long routedBasedOnClusterVersion) {
        this.routedBasedOnClusterVersion = routedBasedOnClusterVersion;
        return (T) this;
    }

    long routedBasedOnClusterVersion() {
        return routedBasedOnClusterVersion;
    }

    @Override
    public ActionRequestValidationException validate() {
        ActionRequestValidationException validationException = null;
        if (index == null) {
            validationException = addValidationError("index is missing", validationException);
        }
        return validationException;
    }

    @Override
    public void readFrom(StreamInput in) throws IOException {
        super.readFrom(in);
        if (in.readBoolean()) {
            shardId = ShardId.readShardId(in);
        } else {
            shardId = null;
        }
        consistencyLevel = WriteConsistencyLevel.fromId(in.readByte());
        timeout = TimeValue.readTimeValue(in);
        index = in.readString();
        canHaveDuplicates = in.readBoolean();
        // no need to serialize threaded* parameters, since they only matter locally
        if (in.getVersion().onOrAfter(Version.V_2_4_0)) {
            routedBasedOnClusterVersion = in.readVLong();
        }
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
        if (shardId != null) {
            out.writeBoolean(true);
            shardId.writeTo(out);
        } else {
            out.writeBoolean(false);
        }
        out.writeByte(consistencyLevel.id());
        timeout.writeTo(out);
        out.writeString(index);
        out.writeBoolean(canHaveDuplicates);
        if (out.getVersion().onOrAfter(Version.V_2_4_0)) {
            out.writeVLong(routedBasedOnClusterVersion);
        }
    }

    @Override
    public Task createTask(long id, String type, String action, TaskId parentTaskId) {
        return new ReplicationTask(id, type, action, getDescription(), parentTaskId);
    }

    /**
     * Sets the target shard id for the request. The shard id is set when a
     * index/delete request is resolved by the transport action
     */
    public T setShardId(ShardId shardId) {
        this.shardId = shardId;
        return (T) this;
    }

    @Override
    public String toString() {
        if (shardId != null) {
            return shardId.toString();
        } else {
            return index;
        }
    }

    @Override
    public String getDescription() {
        return toString();
    }
}
