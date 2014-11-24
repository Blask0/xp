package com.enonic.wem.script.internal;

import java.util.function.Function;

import com.enonic.wem.script.command.CommandHandler;
import com.enonic.wem.script.command.CommandRequest;

public final class TestCommandHandler
    implements CommandHandler
{
    @Override
    public String getName()
    {
        return "test.command";
    }

    @Override
    public Object execute( final CommandRequest req )
    {
        final String name = req.param( "name" ).value( String.class );
        final Function<Object, Object> transform = req.param( "transform" ).callback();

        return transform.apply( name ).toString();
    }
}
