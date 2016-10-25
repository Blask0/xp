package com.enonic.xp.lib.node;

import java.util.Objects;
import java.util.stream.Collectors;

import com.enonic.xp.lib.node.mapper.NodeMapper;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;

public final class GetNodeHandler
    extends BaseNodeHandler
{
    private NodeKey key;

    private NodeKeys keys;

    @Override
    protected Object doExecute()
    {
        if ( key != null )
        {
            return getByKey( key );
        }
        else
        {
            return this.keys.stream().
                map( this::getByKey ).
                filter( Objects::nonNull ).
                collect( Collectors.toList() );
        }
    }

    private NodeMapper getByKey( final NodeKey key )
    {
        if ( key.isId() )
        {
            return getById( key.getAsNodeId() );

        }
        else
        {
            return getByPath( key.getAsPath() );
        }
    }

    private NodeMapper getByPath( final NodePath key )
    {
        try
        {
            return convert( this.nodeService.getByPath( key ) );
        }
        catch ( final NodeNotFoundException e )
        {
            return null;
        }
    }

    private NodeMapper getById( final NodeId key )
    {
        try
        {
            return convert( this.nodeService.getById( key ) );
        }
        catch ( final NodeNotFoundException e )
        {
            return null;
        }
    }

    private NodeMapper convert( final Node content )
    {
        return content == null ? null : new NodeMapper( content );
    }

    public void setKey( final String key )
    {
        this.key = NodeKey.from( key );
    }

    public void setKeys( final String[] keys )
    {
        this.keys = NodeKeys.from( keys );
    }
}
