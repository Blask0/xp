package com.enonic.wem.core.schema.content;

import org.junit.Test;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.content.ContentTypes;

import static org.junit.Assert.*;

public class ContentTypeServiceImpl_getByModuleTest
    extends AbstractContentTypeServiceTest
{
    @Test
    public void testGetByName()
        throws Exception
    {
        register( createContentType( "mymodule:my-contenttype", "DisplayName1" ),
                  createContentType( "othermodule:my-contenttype", "DisplayName2" ) );

        final ContentTypes result = this.service.getByModule( ModuleKey.from( "mymodule" ) );

        assertEquals( 1, result.getSize() );
        verifyContentType( "mymodule:my-contenttype", "DisplayName1", result );
    }
}
