package com.enonic.wem.launcher.home;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

public class HomeResolverTest
    implements HomeConstants
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File validHomeDir;

    private File invalidHomeDir;

    private File missingHomeDir;

    @Before
    public void setUp()
        throws Exception
    {
        this.validHomeDir = this.folder.newFolder( "valid-home" );
        this.invalidHomeDir = this.folder.newFile( "invalid-home" );
        this.missingHomeDir = new File( this.folder.getRoot(), "missing-home" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotSet()
    {
        resolve( null, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidHomeDir()
    {
        assertTrue( this.invalidHomeDir.exists() );
        resolve( this.invalidHomeDir.getAbsolutePath(), null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingHomeDir()
    {
        assertFalse( this.missingHomeDir.exists() );
        resolve( this.missingHomeDir.getAbsolutePath(), null );
    }

    @Test
    public void testSystemProperty()
    {
        assertTrue( this.validHomeDir.exists() );

        final HomeDir homeDir = resolve( this.validHomeDir.getAbsolutePath(), null );
        assertNotNull( homeDir );

        final File homeDirFile = homeDir.toFile();
        assertNotNull( homeDirFile );
        assertTrue( homeDirFile.exists() );
        assertTrue( homeDirFile.isDirectory() );
        assertEquals( this.validHomeDir, homeDirFile );
    }

    @Test
    public void testEnvironment()
    {
        assertTrue( this.validHomeDir.exists() );

        final HomeDir homeDir = resolve( null, this.validHomeDir.getAbsolutePath() );
        assertNotNull( homeDir );

        final File homeDirFile = homeDir.toFile();
        assertNotNull( homeDirFile );
        assertTrue( homeDirFile.exists() );
        assertTrue( homeDirFile.isDirectory() );
        assertEquals( this.validHomeDir, homeDirFile );
    }

    private HomeDir resolve( final String propValue, final String envValue )
    {
        final Properties props = new Properties();
        if ( propValue != null )
        {
            props.put( HOME_DIR_PROP, propValue );
        }

        final Map<String, String> env = new HashMap<>();
        if ( envValue != null )
        {
            env.put( HOME_DIR_ENV, envValue );
        }

        final HomeResolver resolver = new HomeResolver();
        resolver.addSystemProperties( props );
        resolver.addEnvironmentVariables( env );
        return resolver.resolve();
    }
}
