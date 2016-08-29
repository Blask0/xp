package com.enonic.xp.core.impl.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.system.SystemRepoInitializer;

class ApplicationRepoInitializer
{
    private final NodeService nodeService;

    private final RepositoryService repositoryService;

    private static final PrincipalKey SUPER_USER = PrincipalKey.ofUser( UserStoreKey.system(), "su" );

    private static final Logger LOG = LoggerFactory.getLogger( ApplicationRepoInitializer.class );

    public ApplicationRepoInitializer( final NodeService nodeService, final RepositoryService repositoryService )
    {
        this.nodeService = nodeService;
        this.repositoryService = repositoryService;
    }

    public final void initialize()
    {
        runAsAdmin( () -> {

            new SystemRepoInitializer( this.nodeService, this.repositoryService ).initialize();

            if ( isInitialized() )
            {
                LOG.info( "System-repo [applications] layout already initialized" );
                return;
            }

            LOG.info( "Initializing system-repo [applications] layout" );

            initApplicationFolder();

            LOG.info( "System-repo [applications] layout successfully initialized" );

        } );
    }

    private boolean isInitialized()
    {
        return this.nodeService.getByPath( ApplicationRepoServiceImpl.APPLICATION_PATH ) != null;
    }

    private void initApplicationFolder()
    {
        final NodePath applicationsRootPath = ApplicationRepoServiceImpl.APPLICATION_PATH;
        LOG.info( "Initializing [" + applicationsRootPath.toString() + "] folder" );

        nodeService.create( CreateNodeParams.create().
            parent( applicationsRootPath.getParentPath() ).
            name( applicationsRootPath.getLastElement().toString() ).
            inheritPermissions( true ).
            build() );
    }

    private void runAsAdmin( Runnable runnable )
    {
        final User admin = User.create().key( SUPER_USER ).login( "su" ).build();
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( admin ).build();
        ContextBuilder.from( ApplicationConstants.CONTEXT_APPLICATIONS ).authInfo( authInfo ).build().runWith( runnable );
    }

}
