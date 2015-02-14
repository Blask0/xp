package com.enonic.xp.admin.impl.rest.resource.relationship.json;

import com.enonic.xp.core.relationship.RelationshipKey;

public class UpdateRelationshipJson
{
    private String fromContent;

    private String toContent;

    private String type;

    public UpdateRelationshipJson( final RelationshipKey relationshipKey )
    {
        this.fromContent = relationshipKey.getFromContent().toString();
        this.toContent = relationshipKey.getToContent().toString();
        this.type = relationshipKey.getType().toString();
    }

    public String getFromContent()
    {
        return fromContent;
    }

    public String getToContent()
    {
        return toContent;
    }

    public String getType()
    {
        return type;
    }
}
