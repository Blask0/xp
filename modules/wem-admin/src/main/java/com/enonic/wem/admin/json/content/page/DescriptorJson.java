package com.enonic.wem.admin.json.content.page;

import com.google.common.base.Preconditions;

import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.admin.json.form.FormJson;
import com.enonic.wem.api.content.page.Descriptor;


public abstract class DescriptorJson
    implements ItemJson
{
    private final Descriptor descriptor;

    private final FormJson configJson;

    public DescriptorJson( final Descriptor descriptor )
    {
        Preconditions.checkNotNull( descriptor );
        this.descriptor = descriptor;
        this.configJson = new FormJson( descriptor.getConfig() );
    }

    public String getKey()
    {
        return descriptor.getKey().toString();
    }

    public String getName()
    {
        return descriptor.getName() != null ? descriptor.getName().toString() : null;
    }

    public String getDisplayName()
    {
        return descriptor.getDisplayName();
    }

    public FormJson getConfig()
    {
        return configJson;
    }

}
