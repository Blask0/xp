package com.enonic.wem.repo.internal.entity;

import org.junit.Test;

import com.google.common.io.ByteSource;

import com.enonic.wem.api.data.PropertyPath;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.util.BinaryReference;

import static org.junit.Assert.*;

public class GetBinaryCommandTest
    extends AbstractNodeTest
{

    @Test
    public void get_by_reference()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        final BinaryReference imageRef = BinaryReference.from( "myImage" );

        data.addBinaryReferences( "myBinary", imageRef );

        final Node node = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            data( data ).
            attachBinary( imageRef, ByteSource.wrap( "thisIsMyImage".getBytes() ) ).
            build() );

        final ByteSource myImage = GetBinaryCommand.create().
            nodeId( node.id() ).
            binaryReference( imageRef ).
            versionService( this.versionService ).
            workspaceService( this.workspaceService ).
            nodeDao( this.nodeDao ).
            indexService( this.indexService ).
            queryService( this.queryService ).
            binaryBlobStore( this.binaryBlobStore ).
            build().
            execute();

        assertNotNull( myImage );
    }

    @Test
    public void get_by_propertyPath()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        final BinaryReference imageRef = BinaryReference.from( "myImage" );

        data.addBinaryReferences( "myBinary", imageRef );

        final Node node = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            data( data ).
            attachBinary( imageRef, ByteSource.wrap( "thisIsMyImage".getBytes() ) ).
            build() );

        final ByteSource myImage = GetBinaryCommand.create().
            nodeId( node.id() ).
            propertyPath( PropertyPath.from( "myBinary" ) ).
            versionService( this.versionService ).
            workspaceService( this.workspaceService ).
            nodeDao( this.nodeDao ).
            indexService( this.indexService ).
            queryService( this.queryService ).
            binaryBlobStore( this.binaryBlobStore ).
            build().
            execute();

        assertNotNull( myImage );
    }
}