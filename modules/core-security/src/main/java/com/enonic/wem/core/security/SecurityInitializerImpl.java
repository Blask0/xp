package com.enonic.wem.core.security;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeService;
import com.enonic.wem.api.security.CreateRoleParams;
import com.enonic.wem.api.security.CreateUserParams;
import com.enonic.wem.api.security.CreateUserStoreParams;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalRelationship;
import com.enonic.wem.api.security.SecurityInitializer;
import com.enonic.wem.api.security.SecurityService;
import com.enonic.wem.api.security.SystemConstants;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.UserStore;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.api.security.acl.UserStoreAccessControlEntry;
import com.enonic.wem.api.security.acl.UserStoreAccessControlList;

import static com.enonic.wem.api.security.acl.UserStoreAccess.ADMINISTRATOR;
import static com.enonic.wem.api.security.acl.UserStoreAccess.USER_STORE_MANAGER;

@Component(immediate = true)
public final class SecurityInitializerImpl
    implements SecurityInitializer
{
    private final static Logger LOG = LoggerFactory.getLogger( SecurityInitializerImpl.class );

    public static final PrincipalKey ADMIN_USER_KEY = PrincipalKey.ofUser( UserStoreKey.system(), "admin" );

    private SecurityService securityService;

    private NodeService nodeService;

    @Override
    public final void init()
    {
        LOG.info( "Initializing security principals" );

        initializeUserStores();

        final User anonymous = User.anonymous();
        final CreateUserParams createUser = CreateUserParams.create().
            userKey( anonymous.getKey() ).
            displayName( anonymous.getDisplayName() ).
            login( anonymous.getLogin() ).
            email( anonymous.getEmail() ).
            build();
        addUser( createUser );

        final CreateUserParams createAdmin = CreateUserParams.create().
            userKey( ADMIN_USER_KEY ).
            displayName( "Administrator" ).
            login( "admin" ).
            password( "password" ).
            build();
        addUser( createAdmin );

        initializeRoleFolder();

        final CreateRoleParams createEnterpriseAdmin = CreateRoleParams.create().
            roleKey( PrincipalKey.ofEnterpriseAdmin() ).
            displayName( "Enterprise Administrator" ).
            build();
        addRole( createEnterpriseAdmin );

        final CreateRoleParams createUserManager = CreateRoleParams.create().
            roleKey( PrincipalKey.ofRole( "um" ) ).
            displayName( "User Manager" ).
            build();
        addRole( createUserManager );

        final CreateRoleParams createContentManager = CreateRoleParams.create().
            roleKey( PrincipalKey.ofRole( "cm" ) ).
            displayName( "Content Manager" ).
            build();
        addRole( createContentManager );

        addMember( PrincipalKey.ofEnterpriseAdmin(), createAdmin.getKey() );
    }


    private void initializeUserStores()
    {
        final UserStore systemUserStore = securityService.getUserStore( UserStoreKey.system() );
        if ( systemUserStore == null )
        {
            LOG.info( "Initializing user store " + SystemConstants.SYSTEM_USERSTORE.getKey() );

            final UserStoreAccessControlList permissions = UserStoreAccessControlList.of(
                UserStoreAccessControlEntry.create().principal( PrincipalKey.ofEnterpriseAdmin() ).access( ADMINISTRATOR ).build(),
                UserStoreAccessControlEntry.create().principal( ADMIN_USER_KEY ).access( USER_STORE_MANAGER ).build() );

            final CreateUserStoreParams createParams = CreateUserStoreParams.create().
                key( SystemConstants.SYSTEM_USERSTORE.getKey() ).
                displayName( SystemConstants.SYSTEM_USERSTORE.getDisplayName() ).
                permissions( permissions ).build();
            this.securityService.createUserStore( createParams );
        }
    }

    private void addUser( final CreateUserParams createUser )
    {
        try
        {
            if ( !securityService.getUser( createUser.getKey() ).isPresent() )
            {
                securityService.createUser( createUser );
                LOG.info( "User created: " + createUser.getKey().toString() );
            }
        }
        catch ( Throwable t )
        {
            LOG.error( "Unable to initialize user: " + createUser.getKey().toString(), t );
        }
    }

    private void addRole( final CreateRoleParams createRoleParams )
    {
        try
        {
            if ( !securityService.getRole( createRoleParams.getKey() ).isPresent() )
            {
                securityService.createRole( createRoleParams );
                LOG.info( "Role created: " + createRoleParams.getKey().toString() );
            }
        }
        catch ( Throwable t )
        {
            LOG.error( "Unable to initialize role: " + createRoleParams.getKey().toString(), t );
        }
    }

    private void addMember( final PrincipalKey container, final PrincipalKey member )
    {
        try
        {
            securityService.addRelationship( PrincipalRelationship.from( container ).to( member ) );
            LOG.info( "Added " + member + " as member of " + container );
        }
        catch ( Throwable t )
        {
            LOG.error( "Unable to add member: " + container + " -> " + member, t );
        }
    }

    private void initializeRoleFolder()
    {
        final NodePath rolesNodePath = UserStoreNodeTranslator.getRolesNodePath();
        final Node roleNode = SystemConstants.CONTEXT_USER_STORES.callWith( () -> nodeService.getByPath( rolesNodePath ) );

        if ( roleNode == null )
        {
            LOG.info( "Initializing roles folder" );

            SystemConstants.CONTEXT_USER_STORES.callWith( () -> nodeService.create( CreateNodeParams.create().
                parent( rolesNodePath.getParentPath() ).
                name( rolesNodePath.getLastElement().toString() ).
                build() ) );
        }
    }

    @Reference
    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }
}

