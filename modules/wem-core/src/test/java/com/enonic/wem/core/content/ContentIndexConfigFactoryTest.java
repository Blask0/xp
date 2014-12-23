package com.enonic.wem.core.content;

import org.junit.Test;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.data.PropertyPath;
import com.enonic.wem.api.data.PropertySet;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.index.IndexConfig;
import com.enonic.wem.api.index.IndexConfigDocument;
import com.enonic.wem.api.schema.content.ContentTypeName;

import static org.junit.Assert.*;

public class ContentIndexConfigFactoryTest
{
    @Test
    public void testName()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "myString", "myStringValue" );
        final PropertySet metadata = data.addSet( ContentPropertyNames.METADATA );
        metadata.addString( "media", "imageMedia" );
        metadata.addDouble( "double", 13d );
        metadata.addString( "no-index", "no-index-value" );
        final PropertySet subSet = metadata.addSet( "subSet" );
        subSet.addString( "subSetValue", "promp" );

        final CreateContentParams createContentParams = new CreateContentParams();
        createContentParams.type( ContentTypeName.imageMedia() ).
            displayName( "myContent" ).
            name( "my-content" ).
            parent( ContentPath.ROOT ).
            contentData( data );

        final IndexConfigDocument indexConfigDocument = ContentIndexConfigFactory.create( createContentParams );

        assertEquals( IndexConfig.NONE, indexConfigDocument.getConfigForPath(
            PropertyPath.from( ContentPropertyNames.DATA, ContentPropertyNames.METADATA ) ) );

        assertEquals( IndexConfig.MINIMAL, indexConfigDocument.getConfigForPath(
            PropertyPath.from( ContentPropertyNames.DATA, ContentPropertyNames.METADATA, "media" ) ) );

        assertEquals( IndexConfig.NONE, indexConfigDocument.getConfigForPath(
            PropertyPath.from( ContentPropertyNames.DATA, ContentPropertyNames.METADATA, "subSet" ) ) );

        assertEquals( IndexConfig.NONE, indexConfigDocument.getConfigForPath(
            PropertyPath.from( ContentPropertyNames.DATA, ContentPropertyNames.METADATA, "subSet", "subSetValue" ) ) );
    }
}