package com.enonic.wem.core.content.page.region;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.region.LayoutDescriptor;
import com.enonic.wem.api.content.page.region.LayoutDescriptorService;
import com.enonic.wem.api.content.page.region.LayoutDescriptors;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleService;

public final class LayoutDescriptorServiceImpl
    implements LayoutDescriptorService
{
    private ModuleService moduleService;

    public LayoutDescriptor getByKey( final DescriptorKey key )
    {
        return new GetLayoutDescriptorCommand().key( key ).moduleService( this.moduleService ).execute();
    }

    public LayoutDescriptors getByModule( final ModuleKey moduleKey )
    {
        return new GetLayoutDescriptorsByModuleCommand().moduleService( this.moduleService ).moduleKey( moduleKey ).execute();
    }

    public LayoutDescriptors getByModules( final ModuleKeys moduleKeys )
    {
        return new GetLayoutDescriptorsByModulesCommand().moduleService( this.moduleService ).moduleKeys( moduleKeys ).execute();
    }

    public void setModuleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
    }
}
