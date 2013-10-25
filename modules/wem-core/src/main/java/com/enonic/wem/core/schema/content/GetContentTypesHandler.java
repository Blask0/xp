package com.enonic.wem.core.schema.content;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.MixinReferencesToFormItemsTransformer;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;


public final class GetContentTypesHandler
    extends CommandHandler<GetContentTypes>
{
    private ContentTypeDao contentTypeDao;

    @Override
    public void handle()
        throws Exception
    {
        final Session session = context.getJcrSession();
        final ContentTypes contentTypes;
        if ( command.isGetAll() )
        {
            contentTypes = getAllContentTypes( session );
        }
        else
        {
            final QualifiedContentTypeNames qualifiedNames = command.getQualifiedNames();
            contentTypes = getContentTypes( session, qualifiedNames );
        }

        if ( !command.isMixinReferencesToFormItems() )
        {
            command.setResult( contentTypes );
        }
        else
        {
            command.setResult( transformMixinReferences( contentTypes, session ) );
        }
    }

    private ContentTypes getAllContentTypes( final Session session )
    {
        return contentTypeDao.selectAll( session );
    }

    private ContentTypes getContentTypes( final Session session, final QualifiedContentTypeNames contentTypeNames )
    {
        return contentTypeDao.select( contentTypeNames, session );
    }

    private ContentTypes transformMixinReferences( final ContentTypes contentTypes, final Session session )
    {
        final MixinReferencesToFormItemsTransformer transformer = new MixinReferencesToFormItemsTransformer( context.getClient() );
        ContentTypes.Builder transformedContentTypes = ContentTypes.newContentTypes();
        for ( final ContentType contentType : contentTypes )
        {
            final Form transformedForm = transformer.transformForm( contentType.form() );
            final ContentType transformedCty = ContentType.newContentType( contentType ).form( transformedForm ).build();
            transformedContentTypes.add( transformedCty );
        }
        return transformedContentTypes.build();
    }

    @Inject
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }
}
