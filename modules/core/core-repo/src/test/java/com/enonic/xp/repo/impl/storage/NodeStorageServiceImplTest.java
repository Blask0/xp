package com.enonic.xp.repo.impl.storage;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.branch.BranchService;
import com.enonic.xp.repo.impl.node.dao.NodeVersionService;
import com.enonic.xp.repo.impl.version.VersionService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class NodeStorageServiceImplTest
{

    private NodeStorageServiceImpl instance;

    private VersionService versionService;

    private NodeVersionService nodeVersionService;

    private BranchService branchService;

    private NodeId nodeId;

    private NodePath nodePath;

    private NodeVersionId nodeVersionId;

    private InternalContext context;

    private NodeVersionKey versionKey;

    @Before
    public void setUp()
    {
        versionService = Mockito.mock( VersionService.class );
        nodeVersionService = Mockito.mock( NodeVersionService.class );
        branchService = Mockito.mock( BranchService.class );

        instance = new NodeStorageServiceImpl();
        instance.setBranchService( branchService );
        instance.setVersionService( versionService );
        instance.setNodeVersionService( nodeVersionService );
        
        nodeId = NodeId.from( "000-000-000-000" );

        nodePath = NodePath.create( "/path/to/node" ).build();

        nodeVersionId = NodeVersionId.from( "000-000-000-000" );

        versionKey = Mockito.mock( NodeVersionKey.class );

        context = InternalContext.create().
            repositoryId( RepositoryId.from( "repository-id" ) ).
            branch( Branch.from( "branch" ) ).
            build();
    }

    @Test
    public void testGetNode()
    {
        final User admin = User.create().key( PrincipalKey.ofSuperUser() ).login( PrincipalKey.ofSuperUser().getId() ).build();
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( admin ).build();
        final Context appContext  = ContextBuilder.from( ContextAccessor.current() ).authInfo( authInfo ).build();

        appContext.callWith( () -> {
            final NodePath nodePath = NodePath.create( "path" ).build();

            final NodeVersionMetadata nodeVersionMetadata = NodeVersionMetadata.create().
                nodeVersionId( nodeVersionId ).
                nodeVersionKey( versionKey ).
                nodePath( nodePath ).
                build();

            final NodeVersion nodeVersion = NodeVersion.create().
                permissions( AccessControlList.create().
                    add( AccessControlEntry.create().
                        principal( PrincipalKey.ofAnonymous() ).
                        allow( Permission.READ ).
                        build() ).build() ).
                build();

            when( versionService.getVersion( nodeId, nodeVersionId, context ) ).thenReturn( nodeVersionMetadata );
            when( nodeVersionService.get( versionKey, context ) ).thenReturn( nodeVersion );


            final Node result = instance.getNode( nodeId, nodeVersionId, context );

            assertNotNull( result );

            verify( versionService, times( 1 ) ).
                getVersion( any( NodeId.class ), any( NodeVersionId.class ), any( InternalContext.class ) );
            verify( nodeVersionService, times( 1 ) ).
                get( any( NodeVersionKey.class ), any( InternalContext.class ) );
            verifyNoMoreInteractions( versionService, nodeVersionService );

            return null;
        } );
    }

    @Test
    public void testGetNode_NodeVersionMetadataNotFound()
    {
        when( versionService.getVersion( any( NodeId.class ), any( NodeVersionId.class ), any( InternalContext.class ) ) ).
            thenReturn( null );

        final Node result = instance.getNode( nodeId, nodeVersionId, context );

        assertNull( result );

        verify( versionService, times( 1 ) ).
            getVersion( any( NodeId.class ), any( NodeVersionId.class ), any( InternalContext.class ) );
        verifyNoMoreInteractions( versionService );
    }

    @Test
    public void testGetNode_NodeVersionNotFound()
    {
        when( versionService.getVersion( any( NodeId.class ), any( NodeVersionId.class ), any( InternalContext.class ) ) ).
            thenReturn( NodeVersionMetadata.create().build() );

        when( nodeVersionService.get( any( NodeVersionKey.class ), any( InternalContext.class ) ) ).thenReturn( null );

        final Node result = instance.getNode( nodeId, nodeVersionId, context );

        assertNull( result );

        verify( versionService, times( 1 ) ).
            getVersion( any( NodeId.class ), any( NodeVersionId.class ), any( InternalContext.class ) );
        verify( nodeVersionService, times( 1 ) ).
            get( any( NodeVersionKey.class ), any( InternalContext.class ) );

        verifyNoMoreInteractions( versionService, nodeVersionService );
    }

    @Test
    @Ignore
    public void testGetNode_ByPath()
    {
        final NodeBranchEntry nodeBranchEntry = NodeBranchEntry.create().
            nodeId( nodeId ).
            nodePath( nodePath ).
            nodeState( NodeState.DEFAULT ).
            build();

        final NodeVersionMetadata nodeVersionMetadata = NodeVersionMetadata.create().
            nodeVersionId( nodeVersionId ).
            nodeVersionKey( versionKey ).
            nodePath( nodePath ).
            build();

        final NodeVersion nodeVersion = NodeVersion.create().
            permissions( AccessControlList.create().
                add( AccessControlEntry.create().
                    principal( PrincipalKey.ofAnonymous() ).
                    allow( Permission.READ ).
                    build() ).build() ).
            build();

        when( branchService.get( any( NodePath.class ), any( InternalContext.class ) ) ).thenReturn( nodeBranchEntry );
        when( versionService.getVersion( any( NodeId.class ), any( NodeVersionId.class ), any( InternalContext.class ) ) ).
            thenReturn( nodeVersionMetadata );
        when( nodeVersionService.get( any( NodeVersionKey.class ), any( InternalContext.class ) ) ).thenReturn( nodeVersion );

        final Node result = instance.getNode( nodePath, nodeVersionId, context );

        assertNotNull( result );

        verify( branchService, times( 1 ) ).get( any( NodePath.class ), any( InternalContext.class ) );
        verify( versionService, times( 1 ) ).
            getVersion( any( NodeId.class ), any( NodeVersionId.class ), any( InternalContext.class ) );
        verify( nodeVersionService, times( 1 ) ).
            get( any( NodeVersionKey.class ), any( InternalContext.class ) );
        verifyNoMoreInteractions( branchService, versionService, nodeVersionService );
    }

    @Test
    public void testGetNode_ByPathBranchNotFound() {
        when( branchService.get( any( NodePath.class ), any( InternalContext.class ) ) ).thenReturn( null );

        final Node result = instance.getNode( nodePath, nodeVersionId, context );

        assertNull( result );

        verify( branchService, times( 1 ) ).get( any( NodePath.class ), any( InternalContext.class ) );
        verifyNoMoreInteractions( branchService );
    }


}
