package com.enonic.wem.repo.internal.elasticsearch;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;

import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.index.IndexConfig;
import com.enonic.wem.api.index.IndexPath;
import com.enonic.wem.api.index.PatternIndexConfigDocument;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeIndexPath;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeVersionId;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.repo.internal.elasticsearch.document.AbstractStoreDocumentItem;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocument;
import com.enonic.wem.repo.internal.index.IndexValueType;
import com.enonic.wem.repo.internal.repository.IndexNameResolver;

import static com.enonic.wem.repo.internal.TestContext.TEST_REPOSITORY;
import static com.enonic.wem.repo.internal.TestContext.TEST_WORKSPACE;
import static org.junit.Assert.*;

public class NodeStoreDocumentFactoryTest
{

    @Test
    public void validate_given_no_id_then_exception()
        throws Exception
    {
        Node node = Node.newNode().
            build();

        try
        {
            NodeStoreDocumentFactory.createBuilder().
                node( node ).
                nodeVersionId( NodeVersionId.from( "test" ) ).
                workspace( TEST_WORKSPACE ).
                repositoryId( TEST_REPOSITORY.getId() ).
                build().
                create();
        }
        catch ( NullPointerException e )
        {
            assertEquals( "Id must be set", e.getMessage() );
            return;
        }

        fail( "Expected exception" );
    }

    @Test
    public void validate_given_id_then_ok()
    {
        Node node = Node.newNode().
            id( NodeId.from( "abc" ) ).
            build();

        final Collection<StoreDocument> storeDocuments = NodeStoreDocumentFactory.createBuilder().
            node( node ).
            nodeVersionId( NodeVersionId.from( "test" ) ).
            workspace( TEST_WORKSPACE ).
            repositoryId( TEST_REPOSITORY.getId() ).
            build().
            create();

        assertNotNull( storeDocuments );
    }

    @Test
    public void index_node_document_created()
        throws Exception
    {
        Node node = Node.newNode().
            id( NodeId.from( "abc" ) ).
            build();

        final Collection<StoreDocument> storeDocuments = NodeStoreDocumentFactory.createBuilder().
            node( node ).
            nodeVersionId( NodeVersionId.from( "test" ) ).
            workspace( TEST_WORKSPACE ).
            repositoryId( TEST_REPOSITORY.getId() ).
            build().
            create();

        assertNotNull( storeDocuments );
        assertNotNull( getIndexDocumentOfType( storeDocuments, "test" ) );
    }

    @Test
    public void set_analyzer()
        throws Exception
    {
        final String myAnalyzerName = "myAnalyzer";

        final Node node = Node.newNode().
            id( NodeId.from( "abc" ) ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( myAnalyzerName ).
                defaultConfig( IndexConfig.MINIMAL ).
                build() ).
            build();

        final Collection<StoreDocument> storeDocuments = NodeStoreDocumentFactory.createBuilder().
            node( node ).
            nodeVersionId( NodeVersionId.from( "test" ) ).
            workspace( TEST_WORKSPACE ).
            repositoryId( TEST_REPOSITORY.getId() ).
            build().
            create();

        final StoreDocument storeDocument = getIndexDocumentOfType( storeDocuments, "test" );

        assertEquals( myAnalyzerName, storeDocument.getAnalyzer() );
    }

    @Test
    public void node_index_document_meta_data_values()
        throws Exception
    {
        final String myAnalyzerName = "myAnalyzer";

        Instant modifiedDateTime = LocalDateTime.of( 2013, 1, 2, 3, 4, 5 ).toInstant( ZoneOffset.UTC );

        Node node = Node.newNode().
            id( NodeId.from( "myId" ) ).
            parent( NodePath.ROOT ).
            name( NodeName.from( "my-name" ) ).
            createdTime( Instant.now() ).
            creator( PrincipalKey.from( "user:test:creator" ) ).
            modifier( PrincipalKey.from( "user:test:modifier" ) ).
            modifiedTime( modifiedDateTime ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( myAnalyzerName ).
                defaultConfig( IndexConfig.MINIMAL ).
                build() ).
            build();

        final Collection<StoreDocument> storeDocuments = NodeStoreDocumentFactory.createBuilder().
            node( node ).
            nodeVersionId( NodeVersionId.from( "test" ) ).
            workspace( TEST_WORKSPACE ).
            repositoryId( TEST_REPOSITORY.getId() ).
            build().
            create();

        final StoreDocument storeDocument = getIndexDocumentOfType( storeDocuments, "test" );

        assertEquals( myAnalyzerName, storeDocument.getAnalyzer() );
        assertEquals( IndexNameResolver.resolveSearchIndexName( TEST_REPOSITORY.getId() ), storeDocument.getIndexName() );
        assertEquals( "test", storeDocument.getIndexTypeName() );

        final AbstractStoreDocumentItem createdTimeItem =
            getItemWithName( storeDocument, NodeIndexPath.CREATED_TIME, IndexValueType.DATETIME );

        assertEquals( Date.from( node.getCreatedTime() ), createdTimeItem.getValue() );

        final AbstractStoreDocumentItem creator = getItemWithName( storeDocument, NodeIndexPath.CREATOR, IndexValueType.STRING );

        assertEquals( "user:test:creator", creator.getValue() );

        final AbstractStoreDocumentItem modifier = getItemWithName( storeDocument, NodeIndexPath.MODIFIER, IndexValueType.STRING );

        assertEquals( "user:test:modifier", modifier.getValue() );
    }

    @Test
    public void add_properties_then_index_document_items_created_for_each_property()
        throws Exception
    {
        PropertyTree rootDataSet = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        rootDataSet.setDouble( "a.b.c", 2.0 );
        rootDataSet.setLocalDate( "a.b.d", LocalDate.now() );

        Node node = Node.newNode().
            id( NodeId.from( "myId" ) ).
            data( rootDataSet ).
            build();

        final Collection<StoreDocument> storeDocuments = NodeStoreDocumentFactory.createBuilder().
            node( node ).
            nodeVersionId( NodeVersionId.from( "test" ) ).
            workspace( TEST_WORKSPACE ).
            repositoryId( TEST_REPOSITORY.getId() ).
            build().
            create();

        final StoreDocument storeDocument = getIndexDocumentOfType( storeDocuments, "test" );

        assertNotNull( getItemWithName( storeDocument, IndexPath.from( "a_b_c" ), IndexValueType.NUMBER ) );
        assertNotNull( getItemWithName( storeDocument, IndexPath.from( "a_b_d" ), IndexValueType.DATETIME ) );
    }

    @Test
    public void create_for_properties_with_multiple_values()
        throws Exception
    {
        PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.setDouble( "a.b.c", 2.0 );
        rootDataSet.setDouble( "a.b.c[1]", 3.0 );

        Node node = Node.newNode().
            id( NodeId.from( "myId" ) ).
            data( rootDataSet ).
            build();

        final Collection<StoreDocument> storeDocuments = NodeStoreDocumentFactory.createBuilder().
            node( node ).
            nodeVersionId( NodeVersionId.from( "test" ) ).
            workspace( TEST_WORKSPACE ).
            repositoryId( TEST_REPOSITORY.getId() ).
            build().
            create();

        assertTrue( storeDocuments.iterator().hasNext() );
        final StoreDocument next = storeDocuments.iterator().next();

        final Set<AbstractStoreDocumentItem> numberItems = getItemsWithName( next, IndexPath.from( "a.b.c" ), IndexValueType.NUMBER );

        assertEquals( 2, numberItems.size() );

    }

    private StoreDocument getIndexDocumentOfType( final Collection<StoreDocument> storeDocuments, final String indexType )
    {
        for ( StoreDocument storeDocument : storeDocuments )
        {
            if ( indexType.equals( storeDocument.getIndexTypeName() ) )
            {
                return storeDocument;
            }
        }
        return null;
    }

    AbstractStoreDocumentItem getItemWithName( final StoreDocument storeDocument, final IndexPath indexPath, final IndexValueType baseType )
    {
        for ( AbstractStoreDocumentItem item : storeDocument.getStoreDocumentItems() )
        {
            if ( item.getPath().equals( indexPath.getPath() ) && item.getIndexBaseType().equals( baseType ) )
            {
                return item;
            }
        }

        return null;
    }

    Set<AbstractStoreDocumentItem> getItemsWithName( final StoreDocument storeDocument, final IndexPath indexPath,
                                                     final IndexValueType baseType )
    {
        Set<AbstractStoreDocumentItem> items = Sets.newHashSet();

        for ( AbstractStoreDocumentItem item : storeDocument.getStoreDocumentItems() )
        {
            if ( item.getPath().equals( indexPath.getPath() ) && item.getIndexBaseType().equals( baseType ) )
            {
                items.add( item );
            }
        }

        return items;
    }


}
