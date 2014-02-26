package com.enonic.wem.core.relationship.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.relationship.Relationship;
import com.enonic.wem.api.relationship.RelationshipNotFoundException;
import com.enonic.wem.core.index.IndexService;


@Deprecated
final class RelationshipDaoHandlerUpdate
    extends AbstractRelationshipDaoHandler
{
    private Relationship relationship;

    RelationshipDaoHandlerUpdate( final Session session, final IndexService indexService )
    {
        super( session, indexService );
    }

    RelationshipDaoHandlerUpdate relationship( Relationship relationship )
    {
        this.relationship = relationship;
        return this;
    }

    protected void doHandle()
        throws RepositoryException
    {
        final Node node = getRelationshipNode( relationship.getId() );
        if ( node == null )
        {
            throw new RelationshipNotFoundException( relationship.getId() );
        }

        final Relationship existing = relationshipJcrMapper.toRelationship( node );

        relationshipJcrMapper.toJcr( relationship, node );
    }

}
