package com.enonic.xp.script.runtime;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;

public interface ScriptRuntime
{
    ScriptExports execute( ResourceKey script );
}
