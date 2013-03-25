package com.enonic.wem.web.rest.resource.content;

import java.awt.image.BufferedImage;

import com.enonic.wem.api.content.binary.Binary;
import com.enonic.wem.web.rest.resource.BaseImageHelper;

final class ContentImageHelper
    extends BaseImageHelper
{
    public ContentImageHelper()
    {
    }

    public BufferedImage getImageFromBinary( final Binary binary, final int size )
        throws Exception
    {
        if ( binary == null )
        {
            return null;
        }

        final BufferedImage image = toBufferedImage( binary.toByteArray() );
        return resizeImage( image, size );
    }
}
