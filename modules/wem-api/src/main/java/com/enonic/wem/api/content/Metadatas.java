package com.enonic.wem.api.content;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.enonic.wem.api.schema.metadata.MetadataSchemaName;
import com.enonic.wem.api.schema.metadata.MetadataSchemaNames;
import com.enonic.wem.api.support.AbstractImmutableEntitySet;

public final class Metadatas
    extends AbstractImmutableEntitySet<Metadata>
{
    private final ImmutableMap<MetadataSchemaName, Metadata> map;

    private Metadatas( final Set<Metadata> set )
    {
        super( ImmutableSet.copyOf( set ) );
        this.map = Maps.uniqueIndex( set, new ToNameFunction() );
    }

    public MetadataSchemaNames getNames()
    {
        final Collection<MetadataSchemaName> names = Collections2.transform( this.set, new ToNameFunction() );
        return MetadataSchemaNames.from( names );
    }

    public Metadata getMetadata( final MetadataSchemaName name )
    {
        return this.map.get( name );
    }

    public Metadatas copy()
    {
        return Metadatas.from( this.map.values().stream().map( Metadata::copy ).collect( Collectors.toList() ) );
    }

    public static Metadatas empty()
    {
        final ImmutableSet<Metadata> set = ImmutableSet.of();
        return new Metadatas( set );
    }

    public static Metadatas from( final Iterable<? extends Metadata> metadatas )
    {
        return new Metadatas( ImmutableSet.copyOf( metadatas ) );
    }

    public static Metadatas from( final Metadatas metadatas, final Metadata metadata )
    {
        ImmutableSet.Builder<Metadata> builder = ImmutableSet.builder();
        builder.addAll( metadatas );
        builder.add( metadata );
        return new Metadatas( builder.build() );
    }

    private final static class ToNameFunction
        implements Function<Metadata, MetadataSchemaName>
    {
        @Override
        public MetadataSchemaName apply( final Metadata value )
        {
            return value.getName();
        }
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Set<Metadata> set = Sets.newLinkedHashSet();

        public Builder add( final Metadata value )
        {
            set.add( value );
            return this;
        }

        public Metadatas build()
        {
            return new Metadatas( set );
        }
    }
}
