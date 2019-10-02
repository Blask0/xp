package com.enonic.xp.impl.task.cluster;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;

public class TaskTransportResponseHandler
    implements Callable<TaskTransportResponse>, Serializable, HazelcastInstanceAware
{
    private final TaskTransportRequest request;

    private transient TaskTransportRequestHandler handler;

    public TaskTransportResponseHandler( TaskTransportRequest request )
    {
        this.request = request;
    }

    @Override
    public void setHazelcastInstance( final HazelcastInstance hazelcastInstance )
    {
        ConcurrentMap<String, Object> context = hazelcastInstance.getUserContext();
        handler = (TaskTransportRequestHandler) context.get( "TaskTransportRequestHandler" );
    }

    @Override
    public TaskTransportResponse call()
    {
        return handler.messageReceived( request );
    }
}
