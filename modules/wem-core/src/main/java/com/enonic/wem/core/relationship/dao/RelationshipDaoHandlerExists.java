package com.enonic.wem.core.relationship.dao;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.relationship.RelationshipId;
import com.enonic.wem.api.relationship.RelationshipIds;
import com.enonic.wem.core.index.IndexService;


@Deprecated
final class RelationshipDaoHandlerExists
    extends AbstractRelationshipDaoHandler<RelationshipIds>
{
    private RelationshipIds relationshipIds;

    RelationshipDaoHandlerExists( final Session session, final IndexService indexService )
    {
        super( session, indexService );
    }

    RelationshipDaoHandlerExists relationshipIds( RelationshipIds ids )
    {
        this.relationshipIds = ids;
        return this;
    }

    protected void doHandle()
        throws RepositoryException
    {
        final RelationshipIds.Builder builder = RelationshipIds.newRelationshipIds();
        for ( RelationshipId relationshipId : relationshipIds )
        {
            if ( relationshipExists( relationshipId ) )
            {
                builder.add( relationshipId );
            }
        }
        setResult( builder.build() );
    }
}
