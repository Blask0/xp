package com.enonic.wem.export.internal;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.io.ByteSource;

import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.export.NodeExportResult;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeService;
import com.enonic.wem.api.util.BinaryReference;
import com.enonic.wem.export.internal.writer.FileExportWriter;
import com.enonic.wem.export.internal.xml.serializer.XmlNodeSerializer;

import static org.junit.Assert.*;

public class BatchedNodeExportCommandTest
{
    private NodeService nodeService;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before

    public void setUp()
        throws Exception
    {
        this.nodeService = new NodeServiceMock();
    }

    @Test
    public void one_node_file()
        throws Exception
    {
        createNode( "mynode", NodePath.ROOT );

        final NodeExportResult result = BatchedNodeExportCommand.create().
            nodeService( this.nodeService ).
            nodeExportWriter( new FileExportWriter() ).
            exportRootNode( NodePath.ROOT ).
            xmlNodeSerializer( new XmlNodeSerializer() ).
            exportHomePath( this.temporaryFolder.getRoot().toPath() ).
            exportName( "myExport" ).
            build().
            execute();

        assertEquals( 1, result.size() );

        assertFileExists( "/myExport/mynode/_/node.xml" );
    }

    @Test
    public void children_nodes()
        throws Exception
    {
        final Node root = createNode( "mynode", NodePath.ROOT );
        final Node child1 = createNode( "child1", root.path() );
        createNode( "child1_1", child1.path() );
        final Node child1_2 = createNode( "child1_2", child1.path() );
        createNode( "child1_2_1", child1_2.path() );
        createNode( "child1_2_2", child1_2.path() );
        final Node child2 = createNode( "child2", root.path() );
        createNode( "child2_1", child2.path() );

        final NodeExportResult result = BatchedNodeExportCommand.create().
            nodeService( this.nodeService ).
            nodeExportWriter( new FileExportWriter() ).
            exportRootNode( NodePath.ROOT ).
            xmlNodeSerializer( new XmlNodeSerializer() ).
            exportHomePath( this.temporaryFolder.getRoot().toPath() ).
            exportName( "myExport" ).
            build().
            execute();

        assertEquals( 8, result.size() );

        assertFileExists( "/myExport/mynode/_/node.xml" );
        assertFileExists( "/myExport/mynode/child1/_/node.xml" );
        assertFileExists( "/myExport/mynode/child1/child1_1/_/node.xml" );
        assertFileExists( "/myExport/mynode/child1/child1_2/_/node.xml" );
        assertFileExists( "/myExport/mynode/child1/child1_2/child1_2_1/_/node.xml" );
        assertFileExists( "/myExport/mynode/child1/child1_2/child1_2_2/_/node.xml" );
        assertFileExists( "/myExport/mynode/child2/_/node.xml" );
        assertFileExists( "/myExport/mynode/child2/child2_1/_/node.xml" );
    }

    @Test
    public void writerOrderList()
    {
        final Node root = Node.newNode().
            name( NodeName.from( "root" ) ).
            parent( NodePath.ROOT ).
            childOrder( ChildOrder.manualOrder() ).
            build();

        this.nodeService.create( CreateNodeParams.from( root ).build() );

        createNode( "child1", root.path() );
        createNode( "child2", root.path() );
        createNode( "child3", root.path() );
        createNode( "child4", root.path() );
        createNode( "child5", root.path() );
        createNode( "child6", root.path() );

        final NodeExportResult result = BatchedNodeExportCommand.create().
            nodeService( this.nodeService ).
            nodeExportWriter( new FileExportWriter() ).
            exportRootNode( NodePath.ROOT ).
            xmlNodeSerializer( new XmlNodeSerializer() ).
            exportHomePath( this.temporaryFolder.getRoot().toPath() ).
            exportName( "myExport" ).
            build().
            execute();

        assertEquals( 7, result.size() );

        assertFileExists( "/myExport/root/_/node.xml" );
        assertFileExists( "/myExport/root/_/manualChildOrder.txt" );
    }


    @Test
    public void export_from_child_of_child()
        throws Exception
    {
        final Node root = createNode( "mynode", NodePath.ROOT );
        final Node child1 = createNode( "child1", root.path() );
        final Node child1_1 = createNode( "child1_1", child1.path() );
        createNode( "child1_1_1", child1_1.path() );
        createNode( "child1_1_2", child1_1.path() );

        this.nodeService.create( CreateNodeParams.from( root ).build() );

        final NodeExportResult result = BatchedNodeExportCommand.create().
            nodeService( this.nodeService ).
            nodeExportWriter( new FileExportWriter() ).
            exportRootNode( NodePath.newPath( "/mynode/child1/child1_1" ).build() ).
            xmlNodeSerializer( new XmlNodeSerializer() ).
            exportHomePath( this.temporaryFolder.getRoot().toPath() ).
            exportName( "myExport" ).
            build().
            execute();

        assertEquals( 3, result.getExportedNodes().getSize() );

        assertFileExists( "/myExport/child1_1/_/node.xml" );
        assertFileExists( "/myExport/child1_1/child1_1_1/_/node.xml" );
        assertFileExists( "/myExport/child1_1/child1_1_2/_/node.xml" );
    }

    @Test
    public void include_export_root_and_nested_children()
        throws Exception
    {
        final Node root = createNode( "mynode", NodePath.ROOT );
        final Node child1 = createNode( "child1", root.path() );
        createNode( "child2", root.path() );
        final Node child1_1 = createNode( "child1_1", child1.path() );
        createNode( "child1_1_1", child1_1.path() );
        createNode( "child1_1_2", child1_1.path() );

        this.nodeService.create( CreateNodeParams.from( root ).build() );

        final NodeExportResult result = BatchedNodeExportCommand.create().
            nodeService( this.nodeService ).
            nodeExportWriter( new FileExportWriter() ).
            exportRootNode( NodePath.newPath( "/mynode/child1" ).build() ).
            xmlNodeSerializer( new XmlNodeSerializer() ).
            exportHomePath( this.temporaryFolder.getRoot().toPath() ).
            exportName( "myExport" ).
            build().
            execute();

        assertEquals( 4, result.getExportedNodes().getSize() );

        assertFileExists( "/myExport/child1/_/node.xml" );
        assertFileExists( "/myExport/child1/child1_1/_/node.xml" );
        assertFileExists( "/myExport/child1/child1_1/child1_1_1/_/node.xml" );
        assertFileExists( "/myExport/child1/child1_1/child1_1_2/_/node.xml" );
    }

    @Test
    public void create_binary_files()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        final BinaryReference binaryRef1 = BinaryReference.from( "image1.jpg" );
        final BinaryReference binaryRef2 = BinaryReference.from( "image2.jpg" );
        data.addBinaryReference( "my-image-1", binaryRef1 );
        data.addBinaryReference( "my-image-2", binaryRef2 );

        this.nodeService.create( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            data( data ).
            attachBinary( binaryRef1, ByteSource.wrap( "this-is-the-binary-data-for-image1".getBytes() ) ).
            attachBinary( binaryRef2, ByteSource.wrap( "this-is-the-binary-data-for-image2".getBytes() ) ).
            build() );

        final NodeExportResult result = BatchedNodeExportCommand.create().
            nodeService( this.nodeService ).
            nodeExportWriter( new FileExportWriter() ).
            exportRootNode( NodePath.ROOT ).
            xmlNodeSerializer( new XmlNodeSerializer() ).
            exportHomePath( this.temporaryFolder.getRoot().toPath() ).
            exportName( "myExport" ).
            build().
            execute();

        assertEquals( 1, result.getExportedNodes().getSize() );
        assertEquals( 2, result.getExportedBinaries().size() );

        assertFileExists( "/myExport/my-node/_/node.xml" );
        assertFileExists( "/myExport/my-node/_/bin/image1.jpg" );
        assertFileExists( "/myExport/my-node/_/bin/image2.jpg" );
    }


    private Node createNode( final String name, final NodePath root )
    {
        final Node node = Node.newNode().
            name( NodeName.from( name ) ).
            parent( root ).
            build();

        return this.nodeService.create( CreateNodeParams.from( node ).build() );
    }

    private void assertFileExists( final String path )
    {
        assertTrue( "file " + path + " not found", new File( this.temporaryFolder.getRoot().getPath() + path ).exists() );
    }

    private void printPaths()
    {
        final File file = this.temporaryFolder.getRoot();

        doPrintPaths( file );
    }

    private void doPrintPaths( final File file )
    {
        if ( file.isDirectory() )
        {
            final File[] children = file.listFiles();

            for ( final File child : children )
            {
                doPrintPaths( child );
            }
        }
        else
        {
            System.out.println( file.toPath() );
        }
    }

}