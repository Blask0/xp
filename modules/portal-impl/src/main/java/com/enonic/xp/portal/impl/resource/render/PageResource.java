package com.enonic.xp.portal.impl.resource.render;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.xp.portal.impl.resource.underscore.UnderscoreResource;

public final class PageResource
    extends RenderResource
{
    @Path("_")
    public UnderscoreResource underscore()
    {
        return underscore( "/" );
    }

    @Path("{path:.*}/_")
    public UnderscoreResource underscore( @PathParam("path") final String path )
    {
        this.contentPath = ContentPath.from( "/" + path );
        return initResource( new UnderscoreResource() );
    }

    @Path("{path:.*}")
    public PageControllerResource page( @PathParam("path") final String path )
    {
        this.contentPath = ContentPath.from( "/" + path );

        final PageControllerResource resource = initResource( new PageControllerResource() );

        resource.content = getContent( this.contentPath.toString() );
        resource.site = getSite( resource.content );

        if ( resource.content instanceof PageTemplate )
        {
            resource.pageTemplate = (PageTemplate) resource.content;
        }
        else if ( !resource.content.hasPage() )
        {
            resource.pageTemplate = getDefaultPageTemplate( resource.content.getType(), resource.site );
            if ( resource.pageTemplate == null )
            {
                throw notFound( "No template found for content" );
            }
        }
        else
        {
            final Page page = getPage( resource.content );
            resource.pageTemplate = getPageTemplate( page );
        }

        if ( resource.pageTemplate.getController() != null )
        {
            resource.pageDescriptor = getPageDescriptor( resource.pageTemplate );
        }

        final Page effectivePage = new EffectivePageResolver( resource.content, resource.pageTemplate ).resolve();
        final Content effectiveContent = Content.newContent( resource.content ).
            page( effectivePage ).
            build();

        resource.renderer = this.services.getRendererFactory().getRenderer( effectiveContent );
        return resource;
    }
}
