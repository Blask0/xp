package com.enonic.wem.portal.internal.postprocess;

import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.postprocess.PostProcessInstruction;

public final class TestPostProcessInstruction
    implements PostProcessInstruction
{
    @Override
    public String evaluate( final PortalContext context, final String instruction )
    {
        return instruction;
    }
}
