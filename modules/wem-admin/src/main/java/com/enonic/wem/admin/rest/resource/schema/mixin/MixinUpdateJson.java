package com.enonic.wem.admin.rest.resource.schema.mixin;

import com.enonic.wem.admin.json.icon.IconJson;
import com.enonic.wem.api.schema.mixin.MixinName;

public class MixinUpdateJson
{
    private MixinName mixinToUpdate;

    private MixinName name;

    private String config;

    private IconJson icon;

    public MixinName getMixinToUpdate()
    {
        return mixinToUpdate;
    }

    public void setMixinToUpdate( final String mixinToUpdate )
    {
        this.mixinToUpdate = MixinName.from( mixinToUpdate );
    }

    public MixinName getName()
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = MixinName.from( name );
    }

    public String getConfig()
    {
        return config;
    }

    public void setConfig( final String config )
    {
        this.config = config;
    }

    public IconJson getIconJson()
    {
        return icon;
    }

    public void setIcon( final IconJson icon )
    {
        this.icon = icon;
    }
}
