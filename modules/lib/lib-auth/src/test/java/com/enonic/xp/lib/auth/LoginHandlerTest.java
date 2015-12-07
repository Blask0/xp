package com.enonic.xp.lib.auth;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.UserStores;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.Session;
import com.enonic.xp.session.SessionKey;
import com.enonic.xp.session.SimpleSession;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class LoginHandlerTest
    extends ScriptTestSupport
{
    private SecurityService securityService;

    @Override
    public void initialize()
    {
        super.initialize();
        this.securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, this.securityService );

        final SimpleSession session = new SimpleSession( SessionKey.generate() );
        ContextAccessor.current().getLocalScope().setSession( session );
    }

    @Test
    public void testExamples()
    {
        final AuthenticationInfo authInfo = TestDataFixtures.createAuthenticationInfo();

        final UserStores userStores =
            UserStores.from( UserStore.create().displayName( "system" ).key( UserStoreKey.from( "system" ) ).build() );

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );
        Mockito.when( this.securityService.getUserStores() ).thenReturn( userStores );

        runScript( "/site/lib/xp/examples/login.js" );
    }

    @Test
    public void testLoginSuccess()
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( RoleKeys.ADMIN_LOGIN ).build();

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );

        runFunction( "/site/test/login-test.js", "loginSuccess" );

        final Session session = ContextAccessor.current().getLocalScope().getSession();
        final AuthenticationInfo sessionAuthInfo = session.getAttribute( AuthenticationInfo.class );
        Assert.assertEquals( authInfo, sessionAuthInfo );
    }

    @Test
    public void testLoginNoUserStore()
    {
        final UserStores userStores =
            UserStores.from( UserStore.create().displayName( "system" ).key( UserStoreKey.from( "system" ) ).build() );

        final AuthenticationInfo authInfo = TestDataFixtures.createAuthenticationInfo();

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );
        Mockito.when( this.securityService.getUserStores() ).thenReturn( userStores );

        runFunction( "/site/test/login-test.js", "loginNoUserStore" );

        final Session session = ContextAccessor.current().getLocalScope().getSession();
        final AuthenticationInfo sessionAuthInfo = session.getAttribute( AuthenticationInfo.class );
        Assert.assertEquals( authInfo, sessionAuthInfo );
    }

    @Test
    public void testLoginMultipleUserStore()
    {
        final AuthenticationInfo authInfo = TestDataFixtures.createAuthenticationInfo();

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );

        runFunction( "/site/test/login-test.js", "loginMultipleUserStore" );

        final Session session = ContextAccessor.current().getLocalScope().getSession();
        final AuthenticationInfo sessionAuthInfo = session.getAttribute( AuthenticationInfo.class );
        Assert.assertEquals( authInfo, sessionAuthInfo );
    }

    @Test
    public void testInvalidLogin()
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.unAuthenticated();

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );

        runFunction( "/site/test/login-test.js", "invalidLogin" );

        final Session session = ContextAccessor.current().getLocalScope().getSession();
        final AuthenticationInfo sessionAuthInfo = session.getAttribute( AuthenticationInfo.class );
        Assert.assertNull( sessionAuthInfo );
    }
}
