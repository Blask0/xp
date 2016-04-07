package com.enonic.xp.macro;

import com.google.common.annotations.Beta;

import com.enonic.xp.form.Form;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.resource.ResourceKey;

@Beta
public final class MacroDescriptor
{
    private final static String SITE_MACROS_PREFIX = "site/macros/";

    private final MacroKey key;

    private final String displayName;

    private final String description;

    private final Form form;

    private final Icon icon;

    private MacroDescriptor( final Builder builder )
    {
        this.key = builder.key;
        this.displayName = builder.displayName == null ? builder.key.getName() : builder.displayName;
        this.description = builder.description;
        this.form = builder.form == null ? Form.create().build() : builder.form;
        this.icon = builder.icon;
    }

    public MacroKey getKey()
    {
        return key;
    }

    public String getName()
    {
        return key.getName();
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
    }

    public Form getForm()
    {
        return form;
    }

    public Icon getIcon()
    {
        return icon;
    }

    public ResourceKey toResourceKey()
    {
        return ResourceKey.from( key.getApplicationKey(), SITE_MACROS_PREFIX + key.getName() + "/" + key.getName() + ".js" );
    }

    public static ResourceKey toResourceKey( final MacroKey key )
    {
        return ResourceKey.from( key.getApplicationKey(), SITE_MACROS_PREFIX + key.getName() + "/" + key.getName() + ".js" );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private MacroKey key;

        private String displayName;

        private String description;

        private Form form;

        private Icon icon;

        private Builder()
        {
        }

        public Builder key( final MacroKey key )
        {
            this.key = key;
            return this;
        }

        public Builder key( final String key )
        {
            this.key = MacroKey.from( key );
            return this;
        }

        public Builder displayName( final String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder description( final String description )
        {
            this.description = description;
            return this;
        }

        public Builder form( final Form form )
        {
            this.form = form;
            return this;
        }

        public Builder icon( final Icon icon )
        {
            this.icon = icon;
            return this;
        }

        public MacroDescriptor build()
        {
            return new MacroDescriptor( this );
        }
    }

}
