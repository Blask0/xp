package com.enonic.wem.admin.rest.resource.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.enonic.wem.core.config.ConfigProperties;

@Path("ui")
public final class BackgroundImageResource
{
    private ConfigProperties configProperties;

    @GET
    @Path("background.jpg")
    @Produces("image/jpeg")
    public InputStream streamBackgroundImage()
        throws Exception
    {
        final String backgroundImagePath = configProperties.get( "cms.home" ) + "/custom/background.jpg";

        final File source = new File( backgroundImagePath );
        if ( source.exists() )
        {
            return new FileInputStream( source );
        }
        else
        {
            return getClass().getResourceAsStream( "background.jpg" );
        }
    }

    @Inject
    public void setConfigProperties( final ConfigProperties configProperties )
    {
        this.configProperties = configProperties;
    }
}
