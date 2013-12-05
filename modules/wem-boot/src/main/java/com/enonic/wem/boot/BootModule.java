package com.enonic.wem.boot;

import com.google.inject.AbstractModule;

import com.enonic.wem.admin.AdminModule;
import com.enonic.wem.core.CoreModule;
import com.enonic.wem.portal.PortalModule;
import com.enonic.wem.web.WebModule;

final class BootModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        install( new CoreModule() );
        install( new WebModule() );
        install( new AdminModule() );
        install( new PortalModule() );
    }
}
