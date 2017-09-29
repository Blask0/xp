package com.enonic.xp.script.impl.purplejs.bean;

import org.junit.Test;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.impl.purplejs.AbstractScriptTest;

import static org.junit.Assert.*;

public class NewBeanScriptTest
    extends AbstractScriptTest
{
    @Test
    public void testEmpty()
    {
        final ResourceKey script = ResourceKey.from( "myapplication:/bean/simple-test.js" );
        final ScriptExports exports = runTestScript( script );
        assertNotNull( exports );
        assertEquals( script, exports.getScript() );
    }
}
