package com.enonic.wem.api.support;


import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class XmlTestHelper
{
    private final SAXBuilder saxBuilder;

    private final ResourceTestHelper resourceTestHelper;

    public XmlTestHelper( final Object testInstance )
    {
        this.resourceTestHelper = new ResourceTestHelper( testInstance );
        this.saxBuilder = new SAXBuilder();
    }

    public Document loadXml( final String fileName )
    {
        return parse( loadTestFile( fileName ) );
    }

    public String loadTestFile( final String fileName )
    {
        return resourceTestHelper.loadTestFile( fileName );
    }

    public String loadTestXml( final String fileName )
    {
        try
        {
            final URL resource = resourceTestHelper.getTestResource( fileName );
            Document document = parse( resource.openStream() );
            return serialize( document, true );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    public Document parse( final InputStream in )
    {
        try
        {
            return this.saxBuilder.build( in );
        }
        catch ( JDOMException | IOException e )
        {
            throw new RuntimeException( "Failed to parse XML", e );
        }
    }

    public Document parse( final Reader reader )
    {
        try
        {
            return this.saxBuilder.build( reader );
        }
        catch ( JDOMException | IOException e )
        {
            throw new RuntimeException( "Failed to parse XML", e );
        }
    }

    public Document parse( final String xml )
    {
        return parse( new StringReader( xml ) );
    }

    public String parseNSerialize( final String xml, final boolean prettyPrint )
    {
        return serialize( parse( new StringReader( xml ) ), prettyPrint );
    }

    public String serialize( final Document node, final boolean prettyPrint )
    {
        return newSerializer( prettyPrint ).outputString( node );
    }

    public String serialize( final Element node, final boolean prettyPrint )
    {
        return newSerializer( prettyPrint ).outputString( node );
    }

    private XMLOutputter newSerializer( final boolean prettyPrint )
    {
        final Format format = prettyPrint ? Format.getPrettyFormat() : Format.getCompactFormat();
        return new XMLOutputter( format );
    }
}
