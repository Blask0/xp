package com.enonic.xp.repo.impl.node;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.base.Stopwatch;

import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.ResolveSyncWorkResult;

public class PushNodesCommandPerformanceTest
    extends AbstractNodeTest
{
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.createDefaultRootNode();
    }

    @Ignore
    @Test
    public void testReferencePerformance()
        throws Exception
    {
        final Node rootNode = createNode( CreateNodeParams.create().
            name( "rootNode" ).
            parent( NodePath.ROOT ).
            build(), false );

        createNodes( rootNode, 30, 3, 1 );

        refresh();

        final ResolveSyncWorkResult syncWork = ResolveSyncWorkCommand.create().
            nodeId( rootNode.id() ).
            target( CTX_OTHER.getBranch() ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        final Stopwatch started = Stopwatch.createStarted();

        final PushNodesResult result = PushNodesCommand.create().
            ids( syncWork.getNodeComparisons().getNodeIds() ).
            target( CTX_OTHER.getBranch() ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        started.stop();

        final long elapsed = started.elapsed( TimeUnit.SECONDS );
        final int number = result.getSuccessful().getSize();

        System.out.println(
            "Pushed : " + number + " in " + started.toString() + ", " + ( elapsed == 0 ? "n/a" : ( number / elapsed ) + "/s" ) );
    }


}
