package com.enonic.xp.impl.task.cluster;

import java.io.Serializable;
import java.util.concurrent.Callable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class TaskTransportResponseHandler
    implements Callable<TaskTransportResponse>, Serializable
{
    private final TaskTransportRequest request;

    public TaskTransportResponseHandler( TaskTransportRequest request )
    {
        this.request = request;
    }

    @Override
    public TaskTransportResponse call()
    {
        BundleContext bundleContext = FrameworkUtil.getBundle( TaskTransportRequestHandler.class ).
            getBundleContext();
        ServiceReference<TaskTransportRequestHandler> serviceReference = bundleContext.
            getServiceReference( TaskTransportRequestHandler.class );
        TaskTransportRequestHandler handler = bundleContext.getService( serviceReference );
        try
        {
            return handler.messageReceived( request );
        }
        finally
        {
            bundleContext.ungetService( serviceReference );
        }
    }
}
