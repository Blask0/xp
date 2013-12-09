package com.enonic.wem.admin.rest.resource.schema.content;


import com.enonic.wem.api.schema.content.ContentTypeName;

public class ContentTypeCreateJson
{
    private ContentTypeName name;

    private String config;

    private String iconReference;

    public ContentTypeName getName()
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = ContentTypeName.from( name );
    }

    public String getIconReference()
    {
        return iconReference;
    }

    public void setIconReference( final String iconReference )
    {
        this.iconReference = iconReference;
    }

    public String getConfig()
    {
        return config;
    }

    public void setConfig( final String config )
    {
        this.config = config;
    }

}
