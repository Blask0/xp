package com.enonic.wem.repo.internal.elasticsearch;

import java.util.Arrays;

import org.junit.Test;

import com.enonic.wem.api.node.NodeIndexPath;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalKeys;
import com.enonic.wem.repo.internal.index.IndexFieldNameNormalizer;
import com.enonic.wem.repo.internal.index.result.GetResult;
import com.enonic.wem.repo.internal.index.result.SearchResultEntry;
import com.enonic.wem.repo.internal.index.result.SearchResultFieldValue;

import static org.junit.Assert.*;

public class GetResultCanReadResolverTest
{

    @Test
    public void anonymous_no_access()
        throws Exception
    {
        assertFalse( GetResultCanReadResolver.canRead( PrincipalKeys.empty(), new GetResult( SearchResultEntry.create().
            id( "myId" ).
            addField( IndexFieldNameNormalizer.normalize( NodeIndexPath.PERMISSIONS_READ.toString() ),
                      SearchResultFieldValue.value( "user:system:rmy" ) ).
            build() ) ) );
    }

    @Test
    public void anonymous_access()
        throws Exception
    {
        assertTrue( GetResultCanReadResolver.canRead( PrincipalKeys.empty(), new GetResult( SearchResultEntry.create().
            id( "myId" ).
            addField( IndexFieldNameNormalizer.normalize( NodeIndexPath.PERMISSIONS_READ.toString() ),
                      SearchResultFieldValue.value( PrincipalKey.ofAnonymous().toString() ) ).
            build() ) ) );
    }

    @Test
    public void user_access()
        throws Exception
    {
        assertTrue( GetResultCanReadResolver.canRead( PrincipalKeys.from( PrincipalKey.from( "user:system:rmy" ) ),
                                                      new GetResult( SearchResultEntry.create().
                                                          id( "myId" ).
                                                          addField( IndexFieldNameNormalizer.normalize(
                                                                        NodeIndexPath.PERMISSIONS_READ.toString() ),
                                                                    SearchResultFieldValue.value(
                                                                        PrincipalKey.from( "user:system:rmy" ) ) ).
                                                          build() ) ) );
    }

    @Test
    public void group_access()
        throws Exception
    {
        assertTrue( GetResultCanReadResolver.canRead(
            PrincipalKeys.from( PrincipalKey.from( "user:system:rmy" ), PrincipalKey.from( "group:system:my-group" ) ),
            new GetResult( SearchResultEntry.create().
                id( "myId" ).
                addField( IndexFieldNameNormalizer.normalize( NodeIndexPath.PERMISSIONS_READ.toString() ),
                          SearchResultFieldValue.values( Arrays.asList( "user:system:rmy", "group:system:my-group" ) ) ).
                build() ) ) );
    }

}