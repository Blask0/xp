package com.enonic.wem.core.content;


import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.command.content.DeleteContent;
import com.enonic.wem.api.command.entity.DeleteNodeByPath;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.DeleteContentResult;
import com.enonic.wem.api.content.UnableToDeleteContentException;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.entity.DeleteNodeByPathHandler;
import com.enonic.wem.core.index.IndexService;


public class DeleteContentHandler
    extends CommandHandler<DeleteContent>
{
    private ContentDao contentDao;

    private IndexService indexService;

    @Override
    public void handle()
        throws Exception
    {
        deleteContentAsNode();

        doDeleteContent();
    }

    private void doDeleteContent()
        throws RepositoryException
    {
        final Session session = context.getJcrSession();

        // Temporary solution to ease the index-service since content selector are supposed to be rewritten
        final Content contentToDelete = contentDao.selectByPath( command.getContentPath(), session );

        if ( contentToDelete == null )
        {
            command.setResult( DeleteContentResult.NOT_FOUND );
            return;
        }

        try
        {
            contentDao.deleteByPath( command.getContentPath(), session );
            session.save();
            indexService.deleteContent( contentToDelete.getId() );
            command.setResult( DeleteContentResult.SUCCESS );
        }
        catch ( ContentNotFoundException | UnableToDeleteContentException e )
        {
            command.setResult( DeleteContentResult.from( e ) );
        }
    }

    private void deleteContentAsNode()
        throws Exception
    {
        DeleteNodeByPath deleteNodeByPathCommand =
            new DeleteNodeByPath( ContentNodeHelper.translateContentPathToNodePath( command.getContentPath() ) );

        final DeleteNodeByPathHandler deleteNodeByPathHandler = DeleteNodeByPathHandler.
            create().
            command( deleteNodeByPathCommand ).
            context( this.context ).
            indexService( this.indexService ).
            build();

        deleteNodeByPathHandler.handle();
    }


    @Inject
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    @Inject
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }
}
