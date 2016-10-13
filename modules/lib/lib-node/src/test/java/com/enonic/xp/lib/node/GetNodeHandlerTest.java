package com.enonic.xp.lib.node;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;

public class GetNodeHandlerTest
    extends BaseNodeHandlerTest
{
    private void mockGetNode()
    {
        final Node node = createNode();
        Mockito.when( this.nodeService.getById( NodeId.from( "nodeId" ) ) ).
            thenReturn( node );
        Mockito.when( this.nodeService.getByPath( NodePath.create( "/node2-path" ).build() ) ).
            thenReturn( node );
    }

    @Test
    public void testExample()
    {
        mockGetNode();

        Mockito.when( this.repositoryService.get( RepositoryId.from( "cms-repo" ) ) ).
            thenReturn( Repository.create().
                id( RepositoryId.from( "cms-repo" ) ).
                build() );

        runScript( "/site/lib/xp/examples/node/get.js" );
    }

}