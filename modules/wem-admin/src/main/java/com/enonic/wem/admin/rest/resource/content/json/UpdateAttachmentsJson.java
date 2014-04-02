package com.enonic.wem.admin.rest.resource.content.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.admin.json.content.attachment.AttachmentJson;
import com.enonic.wem.api.command.content.attachment.UpdateAttachmentsParams;
import com.enonic.wem.api.content.ContentId;

public class UpdateAttachmentsJson
{
    private UpdateAttachmentsParams updateAttachments;

    @JsonCreator
    public UpdateAttachmentsJson( @JsonProperty("contentId") final String contentId, //
                                  @JsonProperty("attachments") final List<AttachmentJson> attachments )
    {
        final UpdateAttachmentsParams.Builder builder = UpdateAttachmentsParams.newUpdateAttachments( ContentId.from( contentId ) );

        for ( final AttachmentJson attachmentJson : attachments )
        {
            builder.addAttachments( attachmentJson.getAttachment() );
        }

        this.updateAttachments = builder.build();
    }

    @JsonIgnore
    public UpdateAttachmentsParams getUpdateAttachments()
    {
        return updateAttachments;
    }
}
