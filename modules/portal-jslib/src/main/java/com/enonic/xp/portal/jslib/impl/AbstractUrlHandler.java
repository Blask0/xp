package com.enonic.xp.portal.jslib.impl;

import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalContextAccessor;
import com.enonic.xp.portal.url.PortalUrlBuilder;
import com.enonic.xp.portal.url.PortalUrlBuilders;
import com.enonic.wem.script.command.CommandHandler;
import com.enonic.wem.script.command.CommandRequest;

public abstract class AbstractUrlHandler
    implements CommandHandler
{
    private final String name;

    public AbstractUrlHandler( final String name )
    {
        this.name = name;
    }

    @Override
    public final String getName()
    {
        return "portal." + this.name;
    }

    protected final PortalUrlBuilders createBuilders()
    {
        final PortalContext context = PortalContextAccessor.get();
        return new PortalUrlBuilders( context );
    }

    protected abstract PortalUrlBuilder createBuilder( final Multimap<String, String> map );

    @Override
    public final Object execute( final CommandRequest req )
    {
        final Multimap<String, String> map = toMap( req );
        final PortalUrlBuilder builder = createBuilder( map );
        return builder.toString();
    }

    private Multimap<String, String> toMap( final CommandRequest req )
    {
        final Multimap<String, String> map = HashMultimap.create();
        for ( final Map.Entry<String, Object> param : req.getParams().entrySet() )
        {
            final String key = param.getKey();
            if ( key.equals( "params" ) )
            {
                applyParams( map, param.getValue() );
            }
            else
            {
                applyParam( map, "_" + key, param.getValue() );
            }
        }

        return map;
    }

    private void applyParams( final Multimap<String, String> params, final Object value )
    {
        if ( value instanceof Map )
        {
            applyParams( params, (Map) value );
        }
    }

    private void applyParams( final Multimap<String, String> params, final Map<?, ?> value )
    {
        for ( final Map.Entry<?, ?> entry : value.entrySet() )
        {
            final String key = entry.getKey().toString();
            applyParam( params, key, entry.getValue() );
        }
    }

    private void applyParam( final Multimap<String, String> params, final String key, final Object value )
    {
        if ( value instanceof Iterable )
        {
            applyParam( params, key, (Iterable) value );
        }
        else
        {
            params.put( key, value.toString() );
        }
    }

    private void applyParam( final Multimap<String, String> params, final String key, final Iterable values )
    {
        for ( final Object value : values )
        {
            params.put( key, value.toString() );
        }
    }
}
