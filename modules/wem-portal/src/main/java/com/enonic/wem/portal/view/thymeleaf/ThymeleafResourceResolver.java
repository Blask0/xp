package com.enonic.wem.portal.view.thymeleaf;

import java.io.InputStream;
import java.net.URL;

import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.IResourceResolver;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceUrlResolver;

final class ThymeleafResourceResolver
    implements IResourceResolver
{
    public ThymeleafResourceResolver()
    {
    }

    @Override
    public String getName()
    {
        return "module";
    }

    @Override
    public InputStream getResourceAsStream( final TemplateProcessingParameters params, final String resourceName )
    {
        final ResourceKey key = ResourceKey.from( resourceName );
        final URL resourceUrl = ResourceUrlResolver.resolve( key );

        try
        {
            return resourceUrl.openStream();
        }
        catch ( final Exception e )
        {
            return null;
        }
    }
}
