package com.enonic.wem.script.internal.invoker;

import java.util.Map;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.command.CommandParam;
import com.enonic.wem.script.command.CommandRequest;

public final class CommandRequestImpl
    implements CommandRequest
{
    private String name;

    private ResourceKey script;

    private Map<String, Object> paramsMap;

    @Override
    public String getName()
    {
        return this.name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    @Override
    public ResourceKey getScript()
    {
        return this.script;
    }

    public void setScript( final ResourceKey script )
    {
        this.script = script;
    }

    @Override
    public CommandParam param( final String name )
    {
        return new CommandParamImpl( name, this.paramsMap.get( name ) );
    }

    @Override
    public Map<String, Object> paramsMap()
    {
        return this.paramsMap;
    }

    public void setParamsMap( final Map<String, Object> paramsMap )
    {
        this.paramsMap = paramsMap;
    }
}
