package com.enonic.wem.admin.json.schema.content;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.core.schema.content.serializer.ContentTypeXmlSerializer;

public class ContentTypeConfigJson
{
    private final String contentTypeXml;

    public ContentTypeConfigJson( final ContentType contentType )
    {
        this.contentTypeXml = new ContentTypeXmlSerializer().prettyPrint( true ).generateName( false ).toString( contentType );
    }

    public String getContentTypeXml()
    {
        return contentTypeXml;
    }

}
