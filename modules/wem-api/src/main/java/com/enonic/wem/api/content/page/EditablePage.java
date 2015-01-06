package com.enonic.wem.api.content.page;


import com.enonic.wem.api.data.PropertyTree;

public class EditablePage
{
    public DescriptorKey controller;

    public PageTemplateKey template;

    public PageRegions regions;

    public PropertyTree config;

    public EditablePage( final Page source )
    {
        this.controller = source.getController();
        this.template = source.getTemplate();
        this.regions = source.getRegions().copy();
        this.config = source.getConfig().copy();
    }

    public Page build()
    {
        final Page.Builder builder = Page.newPage();
        builder.controller( controller );
        builder.template( template );
        builder.regions( regions );
        builder.config( config );
        return builder.build();
    }
}
