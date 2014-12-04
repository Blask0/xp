package com.enonic.wem.admin.rest.resource.content;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.security.Principal;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.Principals;
import com.enonic.wem.api.security.SecurityService;
import com.enonic.wem.api.security.acl.AccessControlEntry;
import com.enonic.wem.api.security.acl.AccessControlList;

public final class ContentPrincipalsResolver
{
    private final static Logger LOG = LoggerFactory.getLogger( ContentPrincipalsResolver.class );

    private final SecurityService securityService;

    public ContentPrincipalsResolver( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

    public Principals resolveAccessControlListPrincipals( final Content content )
    {
        final AccessControlList acl = content.getAccessControlList();
        final AccessControlList effectiveAcl = content.getEffectiveAccessControlList();
        return resolveAccessControlListPrincipals( acl, effectiveAcl );
    }

    public Principals resolveAccessControlListPrincipals( final AccessControlList... accessControlList )
    {
        final Map<PrincipalKey, Principal> principals = Maps.newHashMap();
        for ( AccessControlList acl : accessControlList )
        {
            if ( acl != null )
            {
                findPrincipals( principals, acl );
            }
        }
        return Principals.from( principals.values() );
    }

    private void findPrincipals( final Map<PrincipalKey, Principal> principals, final AccessControlList acl )
    {
        for ( AccessControlEntry entry : acl )
        {
            final PrincipalKey key = entry.getPrincipal();
            if ( !principals.containsKey( key ) )
            {
                final Optional<? extends Principal> principalValue = securityService.getPrincipal( key );
                if ( !principalValue.isPresent() )
                {
                    LOG.warn( "Principal could not be resolved: " + key.toString() );
                }
                else
                {
                    principals.put( key, principalValue.get() );
                }
            }
        }
    }
}
