package com.enonic.xp.node;

import java.util.Collection;
import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
public final class Nodes
    extends AbstractImmutableEntitySet<Node>
{
    private final ImmutableMap<NodeId, Node> map;

    private Nodes( final Set<Node> set )
    {
        super( ImmutableSet.copyOf( set ) );
        this.map = Maps.uniqueIndex( set, new ToIdFunction() );
    }

    public static Nodes empty()
    {
        final ImmutableSet<Node> set = ImmutableSet.of();
        return new Nodes( set );
    }

    public static Nodes from( final Node... nodes )
    {
        return new Nodes( ImmutableSet.copyOf( nodes ) );
    }

    public static Nodes from( final Iterable<? extends Node> nodes )
    {
        return new Nodes( ImmutableSet.copyOf( nodes ) );
    }

    public static Nodes from( final Collection<? extends Node> nodes )
    {
        return new Nodes( ImmutableSet.copyOf( nodes ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Node getNodeById( final NodeId nodeId )
    {
        return this.map.get( nodeId );
    }

    public NodePaths getPaths()
    {
        final Collection<NodePath> paths = Collections2.transform( this.set, new ToKeyFunction() );
        return NodePaths.from( paths );
    }

    public NodeIds getIds()
    {
        final Collection<NodeId> ids = Collections2.transform( this.set, new ToIdFunction() );
        return NodeIds.from( ids );
    }

    private final static class ToIdFunction
        implements Function<Node, NodeId>
    {
        @Override
        public NodeId apply( final Node value )
        {
            return value.id();
        }
    }

    private final static class ToKeyFunction
        implements Function<Node, NodePath>
    {
        @Override
        public NodePath apply( final Node value )
        {
            return value.path();
        }
    }

    public static class Builder
    {
        private final Set<Node> nodes = Sets.newLinkedHashSet();

        public Builder add( Node node )
        {
            nodes.add( node );
            return this;
        }

        public Builder addAll( Nodes nodes )
        {
            this.nodes.addAll( nodes.getSet() );
            return this;
        }

        public Nodes build()
        {
            return new Nodes( nodes );
        }
    }
}
