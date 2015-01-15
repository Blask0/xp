package com.enonic.wem.core.content.page;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.page.CreatePageParams;
import com.enonic.wem.api.content.page.PageService;
import com.enonic.wem.api.content.page.UpdatePageParams;

@Component(immediate = true)
public final class PageServiceImpl
    implements PageService
{
    private ContentService contentService;

    @Override
    public Content create( final CreatePageParams params )
    {
        return CreatePageCommand.create().
            contentService( this.contentService ).
            params( params ).
            build().
            execute();
    }

    @Override
    public Content update( final UpdatePageParams params )
    {
        return UpdatePageCommand.create().
            contentService( this.contentService ).
            params( params ).
            build().
            execute();
    }

    @Override
    public Content delete( final ContentId contentId )
    {
        return DeletePageCommand.create().
            contentService( this.contentService ).
            contentId( contentId ).
            build().
            execute();
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
