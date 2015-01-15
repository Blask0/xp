package com.enonic.wem.export.internal.xml.util;

import org.apache.commons.lang.StringEscapeUtils;

public class XmlStringEscaper
{
    public static final String escapeContent( final String value )
    {
        return StringEscapeUtils.escapeXml( value );
    }

    public static final String unescapeContent( final String value )
    {
        return StringEscapeUtils.unescapeXml( value );
    }

}
