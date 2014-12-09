package com.enonic.wem.script.internal;

import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;

import com.google.common.collect.Maps;

import jdk.nashorn.api.scripting.JSObject;

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.ScriptObject;
import com.enonic.wem.script.internal.bean.ScriptObjectFactory;
import com.enonic.wem.script.internal.bean.ScriptObjectFactoryImpl;
import com.enonic.wem.script.internal.error.ErrorHelper;
import com.enonic.wem.script.internal.function.ExecuteFunction;
import com.enonic.wem.script.internal.function.RequireFunction;
import com.enonic.wem.script.internal.function.ResolveFunction;
import com.enonic.wem.script.internal.invoker.CommandInvoker;
import com.enonic.wem.script.internal.logger.ScriptLogger;

final class ScriptExecutorImpl
    implements ScriptExecutor
{
    private final ScriptEngine engine;

    private final CommandInvoker invoker;

    private final Map<String, Object> globals;

    private final ScriptObjectFactory scriptObjectFactory;

    public ScriptExecutorImpl( final ScriptEngine engine, final CommandInvoker invoker )
    {
        this.engine = engine;
        this.invoker = invoker;
        this.globals = Maps.newHashMap();
        this.scriptObjectFactory = new ScriptObjectFactoryImpl( this );
    }

    private Bindings createBindings()
    {
        return this.engine.createBindings();
    }

    @Override
    public void addGlobalBinding( final String key, final Object value )
    {
        this.globals.put( key, value );
    }

    private void doExecute( final Bindings bindings, final ResourceKey script )
    {
        try
        {
            final Resource resource = Resource.from( script );
            final String source = resource.readString();

            bindings.put( ScriptEngine.FILENAME, script.toString() );
            this.engine.eval( source, bindings );
        }
        catch ( final Exception e )
        {
            throw ErrorHelper.handleError( e );
        }
    }

    @Override
    public Bindings executeRequire( final ResourceKey script )
    {
        final Bindings bindings = createBindings();
        bindings.putAll( this.globals );

        final Bindings exports = createBindings();
        bindings.put( "exports", exports );

        new ScriptLogger( script ).register( bindings );
        new ResolveFunction( script ).register( bindings );
        new ExecuteFunction( script, this.invoker ).register( bindings );
        new RequireFunction( script, this ).register( bindings );

        doExecute( bindings, script );
        return exports;
    }

    @Override
    public ScriptObject invokeMethod( final Object scope, final JSObject func, final Object... args )
    {
        try
        {
            final Object result = func.call( scope, args );
            return this.scriptObjectFactory.create( result );
        }
        catch ( final Exception e )
        {
            throw ErrorHelper.handleError( e );
        }
    }

    @Override
    public Object convertToJsObject( final Object value )
    {
        return null;
    }
}
