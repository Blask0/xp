package com.enonic.wem.launcher.home;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.enonic.xp.launcher.LauncherException;
import com.enonic.wem.launcher.SharedConstants;
import com.enonic.xp.launcher.env.SystemProperties;

import static org.junit.Assert.*;

public class HomeResolverTest
    implements SharedConstants
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

    @Test(expected = LauncherException.class)
    public void testNotSet()
    {
        resolve( null, null );
    }

    @Test(expected = LauncherException.class)
    public void testInvalidHomeDir()
    {
        assertTrue( this.invalidHomeDir.exists() );
        resolve( this.invalidHomeDir.getAbsolutePath(), null );
    }

    @Test(expected = LauncherException.class)
    public void testMissingHomeDir()
    {
        assertFalse( this.missingHomeDir.exists() );
        resolve( this.missingHomeDir.getAbsolutePath(), null );
    }

    @Test
    public void testSystemProperty()
    {
        assertTrue( this.validHomeDir.exists() );

        final HomeDir homeDir = resolve( null, this.validHomeDir.getAbsolutePath() );
        assertNotNull( homeDir );
        assertEquals( this.validHomeDir.getAbsoluteFile().toString(), homeDir.toString() );

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
        assertEquals( this.validHomeDir.getAbsoluteFile().toString(), homeDir.toString() );

        final File homeDirFile = homeDir.toFile();
        assertNotNull( homeDirFile );
        assertTrue( homeDirFile.exists() );
        assertTrue( homeDirFile.isDirectory() );
        assertEquals( this.validHomeDir, homeDirFile );
    }

    private HomeDir resolve( final String propValue, final String envValue )
    {
        final SystemProperties props = new SystemProperties();

        if ( propValue != null )
        {
            props.put( HOME_PROP, propValue );
        }

        if ( envValue != null )
        {
            props.putEnv( HOME_ENV, envValue );
        }

        final HomeResolver resolver = new HomeResolver( props );
        return resolver.resolve();
    }
}

