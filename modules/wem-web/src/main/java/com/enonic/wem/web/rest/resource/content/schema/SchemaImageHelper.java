package com.enonic.wem.web.rest.resource.content.schema;

import java.awt.image.BufferedImage;

import com.enonic.wem.web.rest.resource.BaseImageHelper;

final class SchemaImageHelper
    extends BaseImageHelper
{
    private final BufferedImage defaultMixinImage;

    private final BufferedImage defaultRelationshipTypeImage;

    public SchemaImageHelper()
        throws Exception
    {
        defaultMixinImage = loadDefaultImage( "mixin" );
        defaultRelationshipTypeImage = loadDefaultImage( "relationshiptype" );
    }

    public BufferedImage getDefaultMixinImage( final int size )
        throws Exception
    {
        return resizeImage( defaultMixinImage, size );
    }

    public BufferedImage getDefaultRelationshipTypeImage( final int size )
        throws Exception
    {
        return resizeImage( defaultRelationshipTypeImage, size );
    }

}
