package com.enonic.xp.repo.impl.ignite;

import org.apache.ignite.Ignite;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.repo.impl.Grid;

@Component
public class IgniteGrid
    implements Grid
{
    private Ignite ignite;

    @Override
    public Object get( final String key )
    {
        return ignite.getOrCreateCache( "pathCache" ).get( key );
    }

    @Override
    public void put( final String key, final Object value )
    {
        ignite.getOrCreateCache( "pathCache" ).put( key, value );
    }

    @Reference
    public void setIgnite( final Ignite ignite )
    {
        this.ignite = ignite;

        System.out.println( " -----------------------------------------------------------------" );
        System.out.println( "--------------------- Setting ignite object (GRID IMPL): " + ignite.hashCode() );
        System.out.println( " -----------------------------------------------------------------" );
    }
}
