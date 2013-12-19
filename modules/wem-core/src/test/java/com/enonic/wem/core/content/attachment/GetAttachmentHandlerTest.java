package com.enonic.wem.core.content.attachment;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.attachment.GetAttachment;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.attachment.dao.AttachmentDao;

import static com.enonic.wem.api.content.attachment.Attachment.newAttachment;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetAttachmentHandlerTest
    extends AbstractCommandHandlerTest
{
    private GetAttachmentHandler handler;

    private AttachmentDao attachmentDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        attachmentDao = Mockito.mock( AttachmentDao.class );
        handler = new GetAttachmentHandler();
        handler.setContext( this.context );
        handler.setAttachmentDao( attachmentDao );
    }

    @Ignore // Due to refactoring of content and attachments
    @Test
    public void getAttachment()
        throws Exception
    {
        // setup
        final Attachment attachment =
            newAttachment().blobKey( new BlobKey( "ABC" ) ).name( "file.jpg" ).mimeType( "image/jpeg" ).label( "small" ).build();
        when( attachmentDao.getAttachmentByPath( isA( ContentPath.class ), isA( String.class ), any( Session.class ) ) ).thenReturn(
            attachment );

        // exercise

        final GetAttachment command =
            Commands.attachment().get().contentPath( ContentPath.from( "myspace:/image" ) ).attachmentName( "file.jpg" );

        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        verify( attachmentDao, atLeastOnce() ).getAttachmentByPath( eq( ContentPath.from( "myspace:/image" ) ), eq( "file.jpg" ),
                                                                    any( Session.class ) );
        assertEquals( attachment, command.getResult() );
    }

}
