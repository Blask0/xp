package com.enonic.wem.core.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ValidateContentData;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.event.EventPublisher;
import com.enonic.wem.api.node.NodeService;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;

abstract class AbstractContentCommand
{
    final ContentNodeTranslator translator;

    final NodeService nodeService;

    final ContentTypeService contentTypeService;

    final EventPublisher eventPublisher;

    AbstractContentCommand( final Builder builder )
    {
        this.contentTypeService = builder.contentTypeService;
        this.nodeService = builder.nodeService;
        this.translator = builder.translator;
        this.eventPublisher = builder.eventPublisher;
    }

    Content getContent( final ContentId contentId )
    {
        return GetContentByIdCommand.create( contentId, this ).
            build().
            execute();
    }

    DataValidationErrors validate( final ContentTypeName contentType, final PropertyTree contentData )
    {
        final ValidateContentData data = new ValidateContentData().contentType( contentType ).contentData( contentData );

        return new ValidateContentDataCommand().contentTypeService( this.contentTypeService ).data( data ).execute();
    }

    public static class Builder<B extends Builder>
    {
        private NodeService nodeService;

        private ContentTypeService contentTypeService;

        private ContentNodeTranslator translator;

        private EventPublisher eventPublisher;

        Builder()
        {

        }

        Builder( final AbstractContentCommand source )
        {
            this.translator = source.translator;
            this.nodeService = source.nodeService;
            this.contentTypeService = source.contentTypeService;
            this.eventPublisher = source.eventPublisher;
        }

        @SuppressWarnings("unchecked")
        public B nodeService( final NodeService nodeService )
        {
            this.nodeService = nodeService;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B translator( final ContentNodeTranslator translator )
        {
            this.translator = translator;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B contentTypeService( final ContentTypeService contentTypeService )
        {
            this.contentTypeService = contentTypeService;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B eventPublisher( final EventPublisher eventPublisher )
        {
            this.eventPublisher = eventPublisher;
            return (B) this;
        }

        void validate()
        {
            Preconditions.checkNotNull( nodeService );
            Preconditions.checkNotNull( contentTypeService );
            Preconditions.checkNotNull( translator );
            Preconditions.checkNotNull( eventPublisher );
        }
    }

}
