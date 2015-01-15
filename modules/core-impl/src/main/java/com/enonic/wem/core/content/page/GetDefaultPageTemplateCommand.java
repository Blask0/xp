package com.enonic.wem.core.content.page;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateSpec;
import com.enonic.wem.api.content.page.PageTemplates;
import com.enonic.wem.api.schema.content.ContentTypeName;

final class GetDefaultPageTemplateCommand
{
    private ContentTypeName contentType;

    private ContentId site;

    private ContentService contentService;

    public PageTemplate execute()
    {

        final PageTemplates pageTemplates = new GetPageTemplateBySiteCommand().
            site( site ).
            contentService( contentService ).
            execute();
        final PageTemplateSpec spec = PageTemplateSpec.newPageTemplateParams().canRender( contentType ).build();
        final PageTemplates supportedTemplates = pageTemplates.filter( spec );
        return supportedTemplates.first();
    }

    public GetDefaultPageTemplateCommand contentType( final ContentTypeName contentType )
    {
        this.contentType = contentType;
        return this;
    }

    public GetDefaultPageTemplateCommand site( final ContentId site )
    {
        this.site = site;
        return this;
    }

    public GetDefaultPageTemplateCommand contentService( final ContentService contentService )
    {
        this.contentService = contentService;
        return this;
    }
}
