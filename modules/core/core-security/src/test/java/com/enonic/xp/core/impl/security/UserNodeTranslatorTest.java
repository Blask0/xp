package com.enonic.xp.core.impl.security;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalType;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;

import static org.junit.Assert.*;

public class UserNodeTranslatorTest
{
    private static final Instant NOW = Instant.ofEpochSecond( 0 );

    private static Clock clock = Clock.fixed( NOW, ZoneId.of( "UTC" ) );

    @Test
    public void toCreateNode()
        throws Exception
    {
        final User user = User.create().
            displayName( "Displayname" ).
            email( "rmy@enonic.com" ).
            login( "Login" ).
            authenticationHash( "password" ).
            key( PrincipalKey.ofUser( UserStoreKey.system(), "Rmy" ) ).
            modifiedTime( Instant.now( clock ) ).
            build();

        final CreateNodeParams createNodeParams = PrincipalNodeTranslator.toCreateNodeParams( user );

        assertEquals( NodeId.from( "user:" + UserStoreKey.system() + ":rmy" ), createNodeParams.getNodeId() );
        assertEquals( "Rmy", createNodeParams.getName() );

        final PropertyTree rootDataSet = createNodeParams.getData();
        assertEquals( UserStoreKey.system().toString(), rootDataSet.getString( PrincipalPropertyNames.USER_STORE_KEY ) );
        assertEquals( PrincipalType.USER.toString(), rootDataSet.getString( PrincipalPropertyNames.PRINCIPAL_TYPE_KEY ) );
        assertEquals( "Displayname", rootDataSet.getString( PrincipalPropertyNames.DISPLAY_NAME_KEY ) );
        assertNotNull( rootDataSet );
        assertEquals( 7, rootDataSet.getTotalSize() );
    }


    @Test
    public void toUser()
        throws Exception
    {
        final PrincipalKey userKey = PrincipalKey.ofUser( UserStoreKey.system(), "i-am-a-user" );

        final PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.setString( PrincipalPropertyNames.LOGIN_KEY, "loginkey" );
        rootDataSet.setString( PrincipalPropertyNames.EMAIL_KEY, "rmy@enonic.com" );
        rootDataSet.setString( PrincipalPropertyNames.DISPLAY_NAME_KEY, "displayname" );
        rootDataSet.setString( PrincipalPropertyNames.PRINCIPAL_TYPE_KEY, userKey.getType().toString() );
        rootDataSet.setString( PrincipalPropertyNames.USER_STORE_KEY, userKey.getUserStore().toString() );
        rootDataSet.setString( PrincipalPropertyNames.AUTHENTICATION_HASH_KEY, "clear:password" );

        final Node node = Node.create().
            id( NodeId.from( "id" ) ).
            name( PrincipalKeyNodeTranslator.toNodeName( userKey ) ).
            data( rootDataSet ).
            build();

        final User user = (User) PrincipalNodeTranslator.fromNode( node );
        assertEquals( "loginkey", user.getLogin() );
        assertEquals( "rmy@enonic.com", user.getEmail() );
        assertEquals( userKey, user.getKey() );
        assertEquals( "clear:password", user.getAuthenticationHash() );
    }
}