package com.enonic.wem.script.internal;

import org.junit.Test;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceProblemException;
import com.enonic.wem.script.AbstractScriptTest;
import com.enonic.wem.script.ScriptExports;

import static org.junit.Assert.*;

public class ScriptServiceImplTest
    extends AbstractScriptTest
{
    @Test
    public void testEmpty()
    {
        final ResourceKey script = ResourceKey.from( "mymodule:/empty-test.js" );
        final ScriptExports exports = runTestScript( script );
        assertNotNull( exports );
        assertSame( script, exports.getScript() );
        assertFalse( exports.hasMethod( "hello" ) );
    }

    @Test
    public void testExecuteExported()
    {
        final ResourceKey script = ResourceKey.from( "mymodule:/export-test.js" );
        final ScriptExports exports = runTestScript( script );
        assertNotNull( exports );
        assertSame( script, exports.getScript() );
        assertTrue( exports.hasMethod( "hello" ) );
        assertEquals( "Hello World!", exports.executeMethod( "hello", "World" ).getValue() );
    }

    @Test
    public void testResolve()
    {
        final ResourceKey script = ResourceKey.from( "mymodule:/resolve/resolve-test.js" );
        final ScriptExports exports = runTestScript( script );
        assertNotNull( exports );
        assertSame( script, exports.getScript() );
    }

    @Test
    public void testRequire()
    {
        final ResourceKey script = ResourceKey.from( "mymodule:/require/require-test.js" );
        final ScriptExports exports = runTestScript( script );
        assertNotNull( exports );
    }

    @Test
    public void testCompileError()
    {
        final ResourceKey script = ResourceKey.from( "mymodule:/error/error-test.js" );

        try
        {
            runTestScript( script );
            fail( "Should throw ResourceProblemException" );
        }
        catch ( final ResourceProblemException e )
        {
            assertEquals( 1, e.getLineNumber() );
            assertEquals( script, e.getResource() );
        }
    }

    @Test
    public void testRuntimeError()
    {
        final ResourceKey script = ResourceKey.from( "mymodule:/error/error-in-export-test.js" );
        final ScriptExports exports = runTestScript( script );

        assertNotNull( exports );

        try
        {
            exports.executeMethod( "hello" );
            fail( "Should throw ResourceProblemException" );
        }
        catch ( final ResourceProblemException e )
        {
            assertEquals( 1, e.getLineNumber() );
            assertEquals( ResourceKey.from( "mymodule:/error/error-test.js" ), e.getResource() );
        }
    }
}
