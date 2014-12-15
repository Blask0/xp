package com.enonic.xp.portal.xslt.impl;

import org.osgi.service.component.annotations.Component;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.command.CommandHandler;
import com.enonic.wem.script.command.CommandRequest;

@Component(immediate = true)
public final class RenderViewHandler
    implements CommandHandler
{
    private final XsltProcessorFactory factory;

    public RenderViewHandler()
    {
        this.factory = new XsltProcessorFactory();
    }

    @Override
    public String getName()
    {
        return "xslt.render";
    }

    @Override
    public Object execute( final CommandRequest req )
    {
        final XsltProcessor processor = this.factory.newProcessor();
        processor.view( req.param( "view" ).required().value( ResourceKey.class ) );
        processor.inputSource( MapToXmlConverter.toSource( req.param( "model" ).map() ) );
        return processor.process();
    }
}
