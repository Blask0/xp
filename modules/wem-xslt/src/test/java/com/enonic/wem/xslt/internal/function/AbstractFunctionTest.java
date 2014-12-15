package com.enonic.wem.xslt.internal.function;

import java.io.StringReader;
import java.net.URL;

import javax.xml.transform.stream.StreamSource;

import org.junit.Before;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceUrlRegistry;
import com.enonic.wem.api.resource.ResourceUrlTestHelper;
import com.enonic.wem.api.xml.DomHelper;
import com.enonic.wem.portal.view.MockViewFunctions;
import com.enonic.wem.xslt.XsltProcessor;
import com.enonic.wem.xslt.internal.XsltProcessorFactoryImpl;

import static org.junit.Assert.*;

public abstract class AbstractFunctionTest
{
    private XsltProcessor processor;

    protected MockViewFunctions viewFunctions;

    @Before
    public final void setup()
    {
        final ResourceUrlRegistry urlRegistry = ResourceUrlTestHelper.mockModuleScheme();
        urlRegistry.modulesClassLoader( getClass().getClassLoader() );

        this.viewFunctions = new MockViewFunctions();

        final XsltProcessorFactoryImpl factory = new XsltProcessorFactoryImpl();
        factory.setViewFunctions( this.viewFunctions );
        this.processor = factory.newProcessor();
    }

    protected final void processTemplate( final String baseName )
        throws Exception
    {
        final String name = "/" + getClass().getName().replace( '.', '/' ) + "-" + baseName;

        this.processor.view( ResourceKey.from( "mymodule:" + name + ".xsl" ) );
        this.processor.inputSource( new StreamSource( new StringReader( "<dummy/>" ) ) );
        final String actual = cleanupXml( this.processor.process() );

        final URL actualUrl = getClass().getResource( name + "-result.xml" );
        final String expected = cleanupXml( Resources.toString( actualUrl, Charsets.UTF_8 ) );

        assertEquals( expected, actual );
    }

    private String cleanupXml( final String xml )
        throws Exception
    {
        return DomHelper.serialize( DomHelper.parse( xml ) );
    }

}
