package com.enonic.wem.api.content;

import java.time.Instant;

import org.junit.Test;

import com.google.common.collect.UnmodifiableIterator;

import com.enonic.wem.api.entity.Workspace;

import static org.junit.Assert.*;

public class GetActiveContentVersionsResultTest
{
    @Test
    public void same_version()
        throws Exception
    {
        final Instant now = Instant.now();

        final ContentVersion version = ContentVersion.create().
            id( ContentVersionId.from( "a" ) ).
            modified( now ).
            build();

        final Workspace stage = Workspace.from( "stage" );
        final Workspace prod = Workspace.from( "prod" );

        final GetActiveContentVersionsResult result = GetActiveContentVersionsResult.create().
            add( ActiveContentVersionEntry.from( stage, version ) ).
            add( ActiveContentVersionEntry.from( prod, version ) ).
            build();

        assertEquals( 2, result.getActiveContentVersions().size() );
    }

    @Test
    public void skip_null()
        throws Exception
    {
        final Instant now = Instant.now();

        final ContentVersion version = ContentVersion.create().
            id( ContentVersionId.from( "a" ) ).
            modified( now ).
            build();

        final Workspace stage = Workspace.from( "stage" );
        final Workspace prod = Workspace.from( "prod" );

        final GetActiveContentVersionsResult result = GetActiveContentVersionsResult.create().
            add( ActiveContentVersionEntry.from( stage, version ) ).
            add( ActiveContentVersionEntry.from( prod, null ) ).
            build();

        assertEquals( 1, result.getActiveContentVersions().size() );
    }

    @Test
    public void test_ordering()
        throws Exception
    {
        final Instant oldest = Instant.parse( "2014-09-25T10:00:00.00Z" );
        final Instant middle = Instant.parse( "2014-09-25T11:00:00.00Z" );
        final Instant newest = Instant.parse( "2014-09-25T12:00:00.00Z" );

        final Workspace archive = Workspace.from( "archive" );
        final Workspace stage = Workspace.from( "stage" );
        final Workspace prod = Workspace.from( "prod" );

        final ContentVersion oldVersion = ContentVersion.create().
            id( ContentVersionId.from( "b" ) ).
            modified( middle ).
            build();

        final ContentVersion oldestVersion = ContentVersion.create().
            id( ContentVersionId.from( "a" ) ).
            modified( oldest ).
            build();

        final ContentVersion newVersion = ContentVersion.create().
            id( ContentVersionId.from( "c" ) ).
            modified( newest ).
            build();

        final GetActiveContentVersionsResult result = GetActiveContentVersionsResult.create().
            add( ActiveContentVersionEntry.from( prod, oldVersion ) ).
            add( ActiveContentVersionEntry.from( stage, newVersion ) ).
            add( ActiveContentVersionEntry.from( archive, oldestVersion ) ).
            build();

        final UnmodifiableIterator<ActiveContentVersionEntry> iterator = result.getActiveContentVersions().iterator();

        assertEquals( stage, iterator.next().getWorkspace() );
        assertEquals( prod, iterator.next().getWorkspace() );
        assertEquals( archive, iterator.next().getWorkspace() );
    }
}