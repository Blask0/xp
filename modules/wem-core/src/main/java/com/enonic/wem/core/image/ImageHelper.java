package com.enonic.wem.core.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

public final class ImageHelper
{
    public static BufferedImage createImage( BufferedImage src, boolean hasAlpha )
    {

        return createImage( src.getWidth(), src.getHeight(), hasAlpha );
    }

    public static BufferedImage createImage( int width, int height, boolean hasAlpha )
    {
        return new BufferedImage( width, height, hasAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB );
    }

    public static BufferedImage getScaledInstance( BufferedImage img, int targetWidth, int targetHeight )
    {
        int width = Math.max( 1, targetWidth );
        int height = Math.max( 1, targetHeight );

        Image scaledImage = img.getScaledInstance( width, height, Image.SCALE_SMOOTH );
        final boolean hasAlpha = img.getTransparency() != Transparency.OPAQUE;
        BufferedImage targetImage = createImage( width, height, hasAlpha );
        Graphics g = targetImage.createGraphics();
        g.drawImage( scaledImage, 0, 0, null );
        g.dispose();
        return targetImage;
    }

    public static BufferedImage removeAlphaChannel( final BufferedImage img, final int color )
    {
        if ( !img.getColorModel().hasAlpha() )
        {
            return img;
        }

        final BufferedImage target = createImage( img, false );
        final Graphics2D g = target.createGraphics();
        g.setColor( new Color( color, false ) );
        g.fillRect( 0, 0, img.getWidth(), img.getHeight() );
        g.drawImage( img, 0, 0, null );
        g.dispose();

        return target;
    }

    public static boolean supportsAlphaChannel( final String format )
    {
        return format.equals( "png" );
    }
}
