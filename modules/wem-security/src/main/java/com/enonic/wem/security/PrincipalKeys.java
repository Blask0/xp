package com.enonic.wem.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.support.AbstractImmutableEntitySet;

import static java.util.stream.Collectors.toSet;

public final class PrincipalKeys
    extends AbstractImmutableEntitySet<PrincipalKey>
{
    private PrincipalKeys( final ImmutableSet<PrincipalKey> list )
    {
        super( list );
    }

    public static PrincipalKeys from( final PrincipalKey... principalKeys )
    {
        return new PrincipalKeys( ImmutableSet.copyOf( principalKeys ) );
    }

    public static PrincipalKeys from( final Iterable<? extends PrincipalKey> principalKeys )
    {
        return new PrincipalKeys( ImmutableSet.copyOf( principalKeys ) );
    }

    public static PrincipalKeys from( final Collection<? extends PrincipalKey> principalKeys )
    {
        return new PrincipalKeys( ImmutableSet.copyOf( principalKeys ) );
    }

    public static PrincipalKeys from( final String... principalKeys )
    {
        return new PrincipalKeys( parsePrincipalKeys( principalKeys ) );
    }

    public static PrincipalKeys empty()
    {
        return new PrincipalKeys( ImmutableSet.<PrincipalKey>of() );
    }

    private static ImmutableSet<PrincipalKey> parsePrincipalKeys( final String... principalKeys )
    {
        final Set<PrincipalKey> principalKeyList = Stream.of( principalKeys ).map( PrincipalKey::from ).collect( toSet() );
        return ImmutableSet.copyOf( principalKeyList );
    }
}
