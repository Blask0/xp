package com.enonic.xp.portal.url;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.xp.portal.PortalContext;

public final class PortalUrlBuilders
{
    private final PortalContext context;

    public PortalUrlBuilders( final PortalContext context )
    {
        this.context = context;
    }

    private ContentPath getContentPath()
    {
        final Content content = this.context.getContent();
        return content != null ? content.getPath() : null;
    }

    private <T extends PortalUrlBuilder> T defaults( final T builder )
    {
        builder.baseUri( this.context.getBaseUri() );
        builder.renderMode( this.context.getMode() );
        builder.workspace( this.context.getWorkspace() );
        builder.contentPath( getContentPath() );
        return builder;
    }

    public GeneralUrlBuilder generalUrl()
    {
        return new GeneralUrlBuilder().baseUri( this.context.getBaseUri() );
    }

    public AssetUrlBuilder assetUrl()
    {
        return defaults( new AssetUrlBuilder() ).module( this.context.getModule() );
    }

    public ImageUrlBuilder imageUrl()
    {
        return defaults( new ImageUrlBuilder() );
    }

    public ServiceUrlBuilder serviceUrl()
    {
        return defaults( new ServiceUrlBuilder() ).module( this.context.getModule() );
    }

    public ComponentUrlBuilder componentUrl()
    {
        return defaults( new ComponentUrlBuilder() );
    }

    public AttachmentUrlBuilder attachmentUrl()
    {
        return defaults( new AttachmentUrlBuilder() );
    }

    public PageUrlBuilder pageUrl()
    {
        return defaults( new PageUrlBuilder() );
    }

    @Deprecated
    public GeneralUrlBuilder createUrl( final String path )
    {
        return generalUrl().contentPath( path );
    }

    @Deprecated
    public AssetUrlBuilder createResourceUrl( final String path )
    {
        return assetUrl().path( path );
    }

    @Deprecated
    public ImageUrlBuilder createImageUrl( final String name )
    {
        return imageUrl().imageName( name );
    }

    @Deprecated
    public ImageUrlBuilder createImageByIdUrl( final ContentId contentId )
    {
        return imageUrl().imageId( contentId );
    }

    @Deprecated
    public ServiceUrlBuilder createServiceUrl( final String name )
    {
        return serviceUrl().service( name );
    }
}
