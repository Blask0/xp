package com.enonic.xp.core.impl.app.resolver;

import java.io.File;
import java.net.URL;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Sets;

import static org.junit.Assert.*;

public class PrefixApplicationUrlResolverTest
{
    private ApplicationUrlResolver delegate;

    private PrefixApplicationUrlResolver resolver;

    @Before
    public void setup()
    {
        this.delegate = Mockito.mock( ApplicationUrlResolver.class );
        this.resolver = new PrefixApplicationUrlResolver( this.delegate, "/a/b/" );
    }

    @Test
    public void testFindFiles()
    {
        Mockito.when( this.delegate.findFiles() ).thenReturn( Sets.newHashSet( "a/b/c.txt", "a/b/c/d.txt", "a/other.txt" ) );

        final Set<String> files = this.resolver.findFiles();
        assertEquals( 2, files.size() );

        assertTrue( files.contains( "c.txt" ) );
        assertTrue( files.contains( "c/d.txt" ) );
    }

    @Test
    public void testFindUrl()
        throws Exception
    {
        final URL expected = new File( "." ).toURI().toURL();
        Mockito.when( this.delegate.findUrl( "a/b/c/d.txt" ) ).thenReturn( expected );

        final URL url1 = this.resolver.findUrl( "c/d.txt" );
        assertSame( expected, url1 );

        final URL url2 = this.resolver.findUrl( "other.txt" );
        assertNull( url2 );
    }
}
