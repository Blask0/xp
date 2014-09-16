package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.GetContentTypesParams;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;


final class GetContentTypesCommand
    extends AbstractContentTypeCommand
{
    private GetContentTypesParams params;

    ContentTypes execute()
    {
        params.validate();

        return doExecute();
    }

    private ContentTypes doExecute()
    {
        final ContentTypes contentTypes = getContentTypes( this.params.getContentTypeNames() );
        if ( !this.params.isMixinReferencesToFormItems() )
        {
            return contentTypes;
        }
        else
        {
            return transformMixinReferences( contentTypes );
        }
    }

    private ContentTypes getContentTypes( final ContentTypeNames contentTypeNames )
    {
        final ContentTypes.Builder contentTypes = ContentTypes.newContentTypes();
        for ( ContentTypeName contentTypeName : contentTypeNames )
        {
            final ContentType.Builder contentTypeBuilder = contentTypeDao.getContentType( contentTypeName );
            if ( contentTypeBuilder != null )
            {
                contentTypes.add( contentTypeBuilder.build() );
            }
        }
        return contentTypes.build();
    }

    GetContentTypesCommand params( final GetContentTypesParams params )
    {
        this.params = params;
        return this;
    }

    GetContentTypesCommand contentTypeDao( final ContentTypeDao contentTypeDao )
    {
        super.contentTypeDao = contentTypeDao;
        return this;
    }

    GetContentTypesCommand mixinService( final MixinService mixinService )
    {
        super.mixinService = mixinService;
        return this;
    }
}
