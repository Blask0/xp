package com.enonic.xp.launcher.provision;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.wiring.BundleRevision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import com.enonic.xp.launcher.config.ConfigProperties;

public final class ProvisionActivator
    implements BundleActivator
{
    private final static Logger LOG = LoggerFactory.getLogger( ProvisionActivator.class );

    private final File systemDir;

    private final ConfigProperties config;

    private BundleContext context;

    public ProvisionActivator( final File systemDir, final ConfigProperties config )
    {
        this.systemDir = systemDir;
        this.config = config;
    }

    @Override
    public void start( final BundleContext context )
        throws Exception
    {
        this.context = context;
        doStart();
    }

    @Override
    public void stop( final BundleContext context )
        throws Exception
    {
        // Do nothing
    }

    private void doStart()
        throws Exception
    {
        installBundles();
    }

    private void installBundles()
        throws Exception
    {
        removeSystemBundles();

        final BundleInfoLoader loader = new BundleInfoLoader( this.systemDir, this.config );
        final List<BundleInfo> list = loader.load();

        LOG.info( "Installing {} bundles...", list.size() );
        for ( final BundleInfo info : list )
        {
            installBundle( info );
        }
    }

    private void installBundle( final BundleInfo info )
        throws Exception
    {
        LOG.debug( "Installing bundle {} at start-level {}", info.getLocation(), info.getLevel() );

        final URI uri = info.getUri( this.systemDir );
        final Bundle bundle = this.context.installBundle( uri.toString() );
        bundle.adapt( BundleStartLevel.class ).setStartLevel( info.getLevel() );
        if ( !isFragmentBundle( bundle ) )
        {
            bundle.start();
        }
    }

    private boolean isFragmentBundle( final Bundle bundle )
    {
        return ( bundle.adapt( BundleRevision.class ).getTypes() & BundleRevision.TYPE_FRAGMENT ) != 0;
    }

    private void removeSystemBundles()
        throws Exception
    {
        LOG.debug( "Uninstalling old system bundles..." );
        for ( final Bundle bundle : this.context.getBundles() )
        {
            removeSystemBundle( bundle );
        }
    }

    private void removeSystemBundle( final Bundle bundle )
        throws Exception
    {
        final String location = bundle.getLocation();
        if ( Strings.isNullOrEmpty( location ) )
        {
            return;
        }

        if ( location.startsWith( this.systemDir.toURI().toString() ) )
        {
            bundle.uninstall();
        }
    }
}
