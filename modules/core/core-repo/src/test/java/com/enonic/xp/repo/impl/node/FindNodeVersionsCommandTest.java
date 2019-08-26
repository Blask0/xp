package com.enonic.xp.repo.impl.node;

import java.time.Instant;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.node.NodeVersionsMetadata;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.repo.impl.version.VersionIndexPath;

import static org.junit.jupiter.api.Assertions.*;

public class FindNodeVersionsCommandTest
    extends AbstractNodeTest
{
    private final Random random = new Random();

    @BeforeEach
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.createDefaultRootNode();
    }

    @Test
    public void get_single_version()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        final NodeVersionQuery query = NodeVersionQuery.create().
            size( 100 ).
            from( 0 ).
            nodeId( node.id() ).
            build();

        final NodeVersionQueryResult result = FindNodeVersionsCommand.create().
            query( query ).
            searchService( this.searchService ).
            build().
            execute();

        assertEquals( 1, result.getHits() );
    }

    @Test
    public void get_multiple_version()
        throws Exception
    {
        PropertyTree data = new PropertyTree();
        data.addLong( "test", this.random.nextLong() );

        final Node node = createNode( CreateNodeParams.create().
            data( data ).
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );
        sleep( 2 );
        doUpdateNode( node );
        sleep( 2 );
        doUpdateNode( node );
        sleep( 2 );
        doUpdateNode( node );
        sleep( 2 );
        doUpdateNode( node );

        refresh();

        final NodeVersionQuery query = NodeVersionQuery.create().
            size( 100 ).
            from( 0 ).
            nodeId( node.id() ).
            addOrderBy( FieldOrderExpr.create( VersionIndexPath.TIMESTAMP, OrderExpr.Direction.DESC ) ).
            build();

        final NodeVersionQueryResult result = FindNodeVersionsCommand.create().
            query( query ).
            searchService( this.searchService ).
            build().
            execute();

        assertEquals( 5, result.getHits() );

        final NodeVersionsMetadata nodeVersionsMetadata = result.getNodeVersionsMetadata();
        Instant previousTimestamp = null;

        for ( final NodeVersionMetadata nodeVersionMetadata : nodeVersionsMetadata )
        {
            if ( previousTimestamp != null )
            {
                if ( !nodeVersionMetadata.getTimestamp().isBefore( previousTimestamp ) )
                {
                    fail( "expected timestamp of current item to be before previous. Previous: [" + previousTimestamp + "], current: [" +
                              nodeVersionMetadata.getTimestamp() + "]" );
                }
            }

            previousTimestamp = nodeVersionMetadata.getTimestamp();
        }
    }

    private void sleep( int ms )
    {
        try
        {
            Thread.sleep( ms );
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
    }

    private Node doUpdateNode( final Node node )
    {
        UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
            id( node.id() ).
            editor( toBeEdited -> toBeEdited.data.setLong( "test", this.random.nextLong() ) ).
            build();

        return UpdateNodeCommand.create().
            params( updateNodeParams ).
            indexServiceInternal( this.indexServiceInternal ).
            binaryService( this.binaryService ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }
}
