package com.enonic.wem.core.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import com.enonic.wem.core.home.HomeDir;

public final class ConfigLoader
{
    private final static Logger LOG = LoggerFactory.getLogger( ConfigLoader.class );

    private final static String CMS_PROPERTIES = "config/cms.properties";

    private final static String DEFAULT_PROPERTIES = "com/enonic/wem/core/config/default.properties";

    private final HomeDir homeDir;

    private final Properties systemProperties;

    private ClassLoader classLoader;

    public ConfigLoader( final HomeDir homeDir )
    {
        this.homeDir = homeDir;
        this.systemProperties = new Properties();
        setClassLoader( getClass().getClassLoader() );

        addSystemProperties( System.getenv() );
        addSystemProperties( System.getProperties() );
    }

    public void addSystemProperties( final Properties props )
    {
        this.systemProperties.putAll( props );
    }

    public void addSystemProperties( final Map<String, String> map )
    {
        this.systemProperties.putAll( map );
    }

    public void setClassLoader( final ClassLoader classLoader )
    {
        this.classLoader = classLoader;
    }

    public ConfigProperties load()
    {
        final Properties props = new Properties();
        props.putAll( loadDefaultProperties() );
        props.putAll( loadCmsProperties() );
        props.putAll( this.homeDir.toProperties() );

        final Properties interpolated = interpolate( props, this.systemProperties );

        final ConfigProperties config = new ConfigPropertiesImpl();
        config.putAll( Maps.fromProperties( interpolated ) );
        return config;
    }

    private Properties loadDefaultProperties()
    {
        final InputStream in = this.classLoader.getResourceAsStream( DEFAULT_PROPERTIES );
        if ( in == null )
        {
            throw new IllegalArgumentException( "Could not find default.properties [" +
                                                    DEFAULT_PROPERTIES + "] in classpath" );
        }

        try
        {
            return loadFromStream( in );
        }
        catch ( final Exception e )
        {
            throw new IllegalArgumentException( "Could not load default.properties [" +
                                                    DEFAULT_PROPERTIES + "] from classpath", e );
        }
    }

    private Properties loadCmsProperties()
    {
        final File file = new File( this.homeDir.toFile(), CMS_PROPERTIES );
        if ( !file.exists() || file.isDirectory() )
        {
            LOG.info( "Could not find cms.properties from [{}]. Using defaults.", file.getAbsolutePath() );
            return new Properties();
        }

        try
        {
            return loadFromStream( new FileInputStream( file ) );
        }
        catch ( final Exception e )
        {
            LOG.error( "Failed to load cms.properties from [{}]. Using defaults.", file.getAbsolutePath() );
        }

        return new Properties();
    }

    private Properties loadFromStream( final InputStream in )
        throws IOException
    {
        final Properties props = new Properties();
        props.load( in );
        in.close();
        return props;
    }

    private Properties interpolate( final Properties props, final Properties env )
    {
        final StrLookup lookup = new StrLookup()
        {
            @Override
            public String lookup( final String key )
            {
                String value = props.getProperty( key );
                if ( !Strings.isNullOrEmpty( value ) )
                {
                    return value;
                }

                return env.getProperty( key );
            }
        };

        final StrSubstitutor substitutor = new StrSubstitutor( lookup );
        final Properties result = new Properties();

        for ( final Object o : props.keySet() )
        {
            final String key = (String) o;
            final String value = props.getProperty( key );
            final String resolved = substitutor.replace( value );
            result.put( key, resolved );
        }

        return result;
    }
}
