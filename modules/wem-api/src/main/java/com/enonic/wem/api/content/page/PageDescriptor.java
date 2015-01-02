package com.enonic.wem.api.content.page;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.page.region.RegionDescriptors;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.resource.ResourceKey;

public final class PageDescriptor
{
    private final DescriptorKey key;

    private final String displayName;

    private final Form config;

    private final RegionDescriptors regions;

    private PageDescriptor( final Builder builder )
    {
        Preconditions.checkNotNull( builder.key, "key cannot be null" );
        Preconditions.checkNotNull( builder.config, "config cannot be null" );
        Preconditions.checkNotNull( builder.regions, "regions cannot be null" );
        this.key = builder.key;
        this.displayName = builder.displayName;
        this.regions = builder.regions;
        this.config = builder.config != null ? builder.config : Form.newForm().build();
    }

    public DescriptorKey getKey()
    {
        return key;
    }

    public ComponentDescriptorName getName()
    {
        return this.key.getName();
    }

    public ResourceKey getResourceKey()
    {
        return ResourceKey.from( key.getModuleKey(), "page/" + key.getName().toString() );
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public Form getConfig()
    {
        return config;
    }

    public RegionDescriptors getRegions()
    {
        return regions;
    }

    public static ResourceKey toResourceKey( final DescriptorKey key )
    {
        return ResourceKey.from( key.getModuleKey(), "page/" + key.getName().toString() + "/page.xml" );
    }
    
    public static PageDescriptor.Builder newPageDescriptor()
    {
        return new Builder();
    }

    public static class Builder
    {
        private DescriptorKey key;

        private String displayName;

        private Form config;

        private RegionDescriptors regions;

        private Builder()
        {
        }

        public Builder key( final DescriptorKey value )
        {
            this.key = value;
            return this;
        }

        public Builder displayName( final String value )
        {
            this.displayName = value;
            return this;
        }

        public Builder config( final Form value )
        {
            this.config = value;
            return this;
        }

        public Builder regions( final RegionDescriptors value )
        {
            this.regions = value;
            return this;
        }

        public PageDescriptor build()
        {
            return new PageDescriptor( this );
        }
    }
}
