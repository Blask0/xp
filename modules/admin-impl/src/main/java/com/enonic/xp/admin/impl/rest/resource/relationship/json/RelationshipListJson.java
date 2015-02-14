package com.enonic.xp.admin.impl.rest.resource.relationship.json;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.relationship.Relationship;
import com.enonic.xp.relationship.Relationships;

public class RelationshipListJson
{
    private final ImmutableList<RelationshipJson> list;

    public RelationshipListJson( final Relationships relationships )
    {
        final ImmutableList.Builder<RelationshipJson> builder = ImmutableList.builder();
        for ( final Relationship model : relationships )
        {
            builder.add( new RelationshipJson( model ) );
        }

        this.list = builder.build();
    }

    public int getTotal()
    {
        return this.list.size();
    }

    public List<RelationshipJson> getRelationships()
    {
        return this.list;
    }
}
