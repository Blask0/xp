package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.GetChildContentTypesParams;

final class GetChildContentTypesCommand
    extends AbstractCommand
{
    protected GetChildContentTypesParams params;

    public ContentTypes execute()
    {
        this.params.validate();
        return doExecute();
    }

    private ContentTypes doExecute()
    {
        final ContentTypes.Builder builder = ContentTypes.newContentTypes();
        final ContentTypes allContentTypes = registry.getAll();

        for ( final ContentType contentType : allContentTypes )
        {
            if ( this.params.getParentName().equals( contentType.getSuperType() ) )
            {
                builder.add( contentType );
            }
        }
        return builder.build();
    }
}
