package com.enonic.xp.portal.impl.resource.service;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.portal.internal.controller.ControllerScript;
import com.enonic.wem.portal.internal.controller.PortalResponseSerializer;
import com.enonic.wem.portal.internal.rendering.RenderResult;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.impl.resource.controller.ControllerResource;

public final class ServiceControllerResource
    extends ControllerResource
{
    protected ResourceKey scriptDir;

    @Override
    protected void configure( final PortalContext context )
    {
        // Do nothing
    }

    @Override
    protected RenderResult execute( final PortalContext context )
        throws Exception
    {
        final ControllerScript controllerScript = this.services.getControllerScriptFactory().newController( this.scriptDir );
        controllerScript.execute( context );

        return new PortalResponseSerializer( context.getResponse() ).serialize();
    }
}
