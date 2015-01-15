package com.enonic.xp.portal.jslib.content;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.FindContentByParentParams;
import com.enonic.wem.api.content.FindContentByParentResult;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.script.mapper.ContentsResultMapper;
import com.enonic.wem.script.command.CommandHandler;
import com.enonic.wem.script.command.CommandRequest;

@Component(immediate = true)
public final class GetChildContentHandler
    implements CommandHandler
{
    static final int DEFAULT_COUNT = 10;

    private ContentService contentService;

    @Override
    public String getName()
    {
        return "content.getChildren";
    }

    @Override
    public Object execute( final CommandRequest req )
    {
        final String key = req.param( "key" ).required().value( String.class );
        final int start = req.param( "start" ).value( Integer.class, 0 );
        final int count = req.param( "count" ).value( Integer.class, DEFAULT_COUNT );
        final String sortExpr = req.param( "sort" ).value( String.class, "" );

        final FindContentByParentParams.Builder findContentByParent = FindContentByParentParams.create();

        if ( key.startsWith( "/" ) )
        {
            findContentByParent.parentPath( ContentPath.from( key ) );
        }
        else
        {
            findContentByParent.parentId( ContentId.from( key ) );
        }

        return getChildren( findContentByParent, start, count, sortExpr );
    }

    private Object getChildren( final FindContentByParentParams.Builder findContentByParent, final int start, final int count,
                                final String sortExpr )
    {
        final ChildOrder childOrder = ChildOrder.from( sortExpr );

        try
        {
            final FindContentByParentResult result = this.contentService.findByParent( findContentByParent.
                from( start ).
                size( count ).
                childOrder( childOrder ).build() );
            return convert( result );
        }
        catch ( final ContentNotFoundException e )
        {
            return new ContentsResultMapper( Contents.empty(), 0 );
        }
    }

    private Object convert( final FindContentByParentResult findContentResult )
    {
        return new ContentsResultMapper( findContentResult.getContents(), findContentResult.getTotalHits() );
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
