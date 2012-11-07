package com.enonic.wem.core.content;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContentType;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.dao.ContentTypeDao;

import static org.junit.Assert.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class CreateContentTypeHandlerTest
    extends AbstractCommandHandlerTest
{
    private CreateContentTypeHandler handler;

    private ContentTypeDao contentTypeDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        contentTypeDao = Mockito.mock( ContentTypeDao.class );
        handler = new CreateContentTypeHandler();
        handler.setContentTypeDao( contentTypeDao );
    }

    @Test
    public void createContentType()
        throws Exception
    {
        // setup
        final ContentType contentType = new ContentType();
        contentType.setName( "myContentType" );
        contentType.setModule( new Module("myModule") );
        contentType.setDisplayName( "My content type" );
        contentType.setAbstract( false );

        // exercise
        final CreateContentType command = Commands.contentType().create().contentType( contentType );
        this.handler.handle( this.context, command );

        // verify
        verify( contentTypeDao, atLeastOnce() ).createContentType( Mockito.any( Session.class ), Mockito.isA( ContentType.class ) );
        final QualifiedContentTypeName contentTypeName = command.getResult();
        assertNotNull( contentTypeName );
        assertEquals( "myModule:myContentType", contentTypeName.toString() );
    }

}
