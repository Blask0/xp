package com.enonic.wem.core.content.page;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptors;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleService;

final class GetPageDescriptorsByModuleCommand
    extends AbstractGetPageDescriptorCommand
{
    private final static Pattern PATTERN = Pattern.compile( "page/([^/]+)/page.xml" );

    private ModuleKey moduleKey;

    private ModuleService moduleService;

    public PageDescriptors execute()
    {
        final Module module = this.moduleService.getModule( this.moduleKey );
        return getDescriptorsFromModule( module );
    }

    public GetPageDescriptorsByModuleCommand moduleKey( final ModuleKey moduleKey )
    {
        this.moduleKey = moduleKey;
        return this;
    }

    public final GetPageDescriptorsByModuleCommand moduleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
        return this;
    }

    private PageDescriptors getDescriptorsFromModule( final Module module )
    {
        final List<PageDescriptor> pageDescriptors = new ArrayList<>();
        final List<String> componentNames = module.getResourcePaths().stream().
            map( PATTERN::matcher ).
            filter( Matcher::matches ).
            map( ( matcher ) -> matcher.group( 1 ) ).
            collect( Collectors.toList() );

        for ( final String componentName : componentNames )
        {
            final DescriptorKey key = DescriptorKey.from( module.getKey(), componentName );
            final PageDescriptor pageDescriptor = getDescriptor( key );
            if ( pageDescriptor != null )
            {
                pageDescriptors.add( pageDescriptor );
            }
        }

        return PageDescriptors.from( pageDescriptors );
    }

}