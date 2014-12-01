package com.enonic.wem.repo.internal.entity;

import java.time.Instant;
import java.time.LocalDate;

import org.junit.Test;

import com.enonic.wem.api.data2.PropertyTree;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.index.IndexConfig;
import com.enonic.wem.api.index.PatternIndexConfigDocument;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.security.PrincipalKey;

import static org.junit.Assert.*;

public class NodeEditTest
{

    @Test
    public void edit_nothing()
        throws Exception
    {
        final Node node = createNode();

        final Node editedNode = Node.editNode( node ).build();

        assertEquals( node, editedNode );
    }

    @Test
    public void edit_name()
        throws Exception
    {
        final Node node = createNode();

        final NodeName newName = NodeName.from( "newname" );

        final Node editedNode = Node.editNode( node ).
            name( newName ).
            build();

        assertEquals( newName, editedNode.name() );
    }

    @Test
    public void edit_index_config()
        throws Exception
    {
        final Node node = createNode();

        final PatternIndexConfigDocument newIndexConfig = PatternIndexConfigDocument.create().
            defaultConfig( IndexConfig.FULLTEXT ).
            build();

        final Node editedNode = Node.editNode( node ).
            indexConfigDocument( newIndexConfig ).
            build();

        assertEquals( newIndexConfig, editedNode.getIndexConfigDocument() );

    }


    private Node createNode()
    {
        PropertyTree rootDataSet = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        rootDataSet.setDouble( "a.b.c", 2.0 );
        rootDataSet.setLocalDate( "b", LocalDate.now() );
        rootDataSet.setString( "c", "runar" );

        return Node.newNode().
            id( NodeId.from( "node" ) ).
            parent( NodePath.ROOT ).
            name( NodeName.from( "mynode" ) ).
            creator( PrincipalKey.from( "user:system:admin" ) ).
            createdTime( Instant.now() ).
            data( rootDataSet ).
            childOrder( ChildOrder.from( "modifiedTime DESC" ) ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                defaultConfig( IndexConfig.MINIMAL ).
                build() ).
            build();
    }
}