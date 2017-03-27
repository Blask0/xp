package com.enonic.xp.region;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.descriptor.Descriptor;
import com.enonic.xp.form.Form;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.ResourceKey;

@Beta
public abstract class ComponentDescriptor
    extends Descriptor
{
    private final String displayName;

    private final Form config;

    ComponentDescriptor( final BaseBuilder builder )
    {
        super( builder.key );

        Preconditions.checkNotNull( builder.name, "name cannot be null" );
        Preconditions.checkNotNull( builder.config, "config cannot be null" );
        this.displayName = builder.displayName == null || builder.displayName.trim().isEmpty() ? builder.name : builder.displayName;
        this.config = builder.config;
    }

    public final String getDisplayName()
    {
        return displayName;
    }

    public final Form getConfig()
    {
        return config;
    }

    public abstract ResourceKey getComponentPath();

    public abstract static class BaseBuilder<T extends BaseBuilder>
    {
        protected DescriptorKey key;

        protected String name;

        protected String displayName;

        protected Form config;

        BaseBuilder()
        {
        }

        BaseBuilder( final ComponentDescriptor descriptor )
        {
            this.key = descriptor.getKey();
            this.name = descriptor.getName();
            this.displayName = descriptor.getDisplayName();
            this.config = descriptor.getConfig();
        }

        public final T key( final DescriptorKey key )
        {
            this.key = key;
            return typecastToBuilder( this );
        }

        public final T displayName( final String displayName )
        {
            this.displayName = displayName;
            return typecastToBuilder( this );
        }

        public final T config( final Form value )
        {
            this.config = value;
            return typecastToBuilder( this );
        }

        @SuppressWarnings("unchecked")
        private T typecastToBuilder( final BaseBuilder object )
        {
            return (T) object;
        }
    }
}
