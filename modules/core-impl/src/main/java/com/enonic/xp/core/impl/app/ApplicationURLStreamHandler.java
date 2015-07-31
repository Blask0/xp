package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.url.AbstractURLStreamHandlerService;
import org.osgi.service.url.URLStreamHandlerService;

import com.google.common.base.Strings;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;

import static org.osgi.framework.BundleEvent.INSTALLED;
import static org.osgi.framework.BundleEvent.UNINSTALLED;

@Component(immediate = true, service = URLStreamHandlerService.class, property = {"url.handler.protocol=module"})
public final class ApplicationURLStreamHandler
    extends AbstractURLStreamHandlerService
{
    private BundleContext bundleContext;

    private final ConcurrentHashMap<String, Long> applicationNameToBundleIdCache;

    public ApplicationURLStreamHandler()
    {
        this.applicationNameToBundleIdCache = new ConcurrentHashMap<>();
    }

    @Override
    public URLConnection openConnection( final URL url )
        throws IOException
    {
        final String path = url.getPath();
        if ( Strings.isNullOrEmpty( path ) )
        {
            throw new MalformedURLException( "Path can not be null or empty." );
        }

        final ResourceKey key = ResourceKey.from( path );
        final Bundle bundle = getBundle( key.getApplicationKey() );

        final URL resolvedUrl = bundle.getResource( key.getPath() );
        return resolvedUrl != null ? resolvedUrl.openConnection() : null;
    }

    private Bundle getBundle( final ApplicationKey key )
        throws IOException
    {
        final String applicationName = key.toString();
        final Long bundleId = this.applicationNameToBundleIdCache.computeIfAbsent( applicationName, this::findBundleId );
        if ( bundleId == null )
        {
            throw new IOException( "Application [" + key.toString() + "] does not exist" );
        }
        return this.bundleContext.getBundle( bundleId );
    }

    private Long findBundleId( final String applicationName )
    {
        final Bundle bundle = findBundle( applicationName );
        return bundle == null ? null : bundle.getBundleId();
    }

    /**
     * Find bundle by Application name. If multiple matching bundles are found, return the one with higher version.
     */
    private Bundle findBundle( final String applicationName )
    {
        return Arrays.stream( this.bundleContext.getBundles() ).
            filter( bundle -> bundle.getSymbolicName().equals( applicationName ) ).
            sorted( ( b1, b2 ) -> b1.getVersion().compareTo( b2.getVersion() ) ).
            findFirst().
            orElse( null );
    }

    private void invalidateCache( final BundleEvent bundleEvent )
    {
        final int eventType = bundleEvent.getType();
        if ( ( eventType == UNINSTALLED ) || ( eventType == INSTALLED ) )
        {
            final String applicationName = bundleEvent.getBundle().getSymbolicName();
            this.applicationNameToBundleIdCache.remove( applicationName );
        }
    }

    @Activate
    public void activate( final BundleContext context )
    {
        this.bundleContext = context;
        this.bundleContext.addBundleListener( this::invalidateCache );
    }
}