package com.enonic.wem.core.content.attachment.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.content.attachment.Attachment;

import static com.enonic.wem.core.jcr.JcrHelper.getPropertyLong;
import static com.enonic.wem.core.jcr.JcrHelper.getPropertyString;

final class AttachmentJcrMapper
{
    static final String NAME = "name";

    static final String MIME_TYPE = "mimeType";

    static final String BLOB_KEY = "blobKey";

    static final String SIZE = "size";

    static final String LABEL = "label";

    void toJcr( final Attachment attachment, final Node attachmentNode )
        throws RepositoryException
    {
        attachmentNode.setProperty( NAME, attachment.getName() );
        attachmentNode.setProperty( MIME_TYPE, attachment.getMimeType() );
        attachmentNode.setProperty( SIZE, attachment.getSize() );
        attachmentNode.setProperty( BLOB_KEY, attachment.getBlobKey().toString() );
        if ( attachment.getLabel() != null )
        {
            attachmentNode.setProperty( LABEL, attachment.getLabel() );
        }
    }

    void toAttachment( final Node attachmentNode, final Attachment.Builder attachmentBuilder )
        throws RepositoryException
    {
        attachmentBuilder.name( getPropertyString( attachmentNode, NAME ) );
        attachmentBuilder.mimeType( getPropertyString( attachmentNode, MIME_TYPE ) );
        attachmentBuilder.label( getPropertyString( attachmentNode, LABEL ) );
        attachmentBuilder.size( getPropertyLong( attachmentNode, SIZE ) );
        attachmentBuilder.blobKey( new BlobKey( getPropertyString( attachmentNode, BLOB_KEY ) ) );
    }

}
