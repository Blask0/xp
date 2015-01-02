package com.enonic.wem.api.content.page.part;


import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.support.AbstractImmutableEntityList;

public final class PartDescriptors
    extends AbstractImmutableEntityList<PartDescriptor>
{
    private final ImmutableMap<ComponentDescriptorName, PartDescriptor> descriptorsByName;

    private final ImmutableMap<DescriptorKey, PartDescriptor> descriptorsByKey;

    private PartDescriptors( final ImmutableList<PartDescriptor> list )
    {
        super( list );
        this.descriptorsByName = Maps.uniqueIndex( list, new ToNameFunction() );
        this.descriptorsByKey = Maps.uniqueIndex( list, new ToKeyFunction() );
    }

    public PartDescriptor getDescriptor( final DescriptorKey key )
    {
        return this.descriptorsByKey.get( key );
    }

    public PartDescriptor getDescriptor( final ComponentDescriptorName name )
    {
        return this.descriptorsByName.get( name );
    }

    public static PartDescriptors empty()
    {
        final ImmutableList<PartDescriptor> list = ImmutableList.of();
        return new PartDescriptors( list );
    }

    public static PartDescriptors from( final PartDescriptor... descriptors )
    {
        return new PartDescriptors( ImmutableList.copyOf( descriptors ) );
    }

    public static PartDescriptors from( final Iterable<? extends PartDescriptor> descriptors )
    {
        return new PartDescriptors( ImmutableList.copyOf( descriptors ) );
    }

    public static PartDescriptors from( final Collection<? extends PartDescriptor> descriptors )
    {
        return new PartDescriptors( ImmutableList.copyOf( descriptors ) );
    }

    private final static class ToNameFunction
        implements Function<PartDescriptor, ComponentDescriptorName>
    {
        @Override
        public ComponentDescriptorName apply( final PartDescriptor value )
        {
            return value.getName();
        }
    }

    private final static class ToKeyFunction
        implements Function<PartDescriptor, DescriptorKey>
    {
        @Override
        public DescriptorKey apply( final PartDescriptor value )
        {
            return value.getKey();
        }
    }

    public static Builder newPartDescriptors()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ImmutableList.Builder<PartDescriptor> list = new ImmutableList.Builder<>();

        private Builder()
        {
        }

        public Builder add( PartDescriptor descriptor )
        {
            this.list.add( descriptor );
            return this;
        }

        public PartDescriptors build()
        {
            return new PartDescriptors( this.list.build() );
        }
    }

}
