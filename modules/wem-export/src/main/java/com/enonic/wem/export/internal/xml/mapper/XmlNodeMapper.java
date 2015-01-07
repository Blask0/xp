package com.enonic.wem.export.internal.xml.mapper;

import com.enonic.wem.api.export.ExportNodeException;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.export.internal.xml.XmlNode;

public class XmlNodeMapper
{
    public static XmlNode toXml( final Node node, final boolean exportNodeIds )
    {
        try
        {
            return doSerializeNode( node, exportNodeIds );
        }
        catch ( Exception e )
        {
            throw new ExportNodeException( "Failed to serialize node to xml", e );
        }
    }

    private static XmlNode doSerializeNode( final Node node, final boolean exportNodeIds )
    {
        final XmlNode xml = new XmlNode();

        if ( exportNodeIds )
        {
            xml.setId( node.id().toString() );
        }
        xml.setChildOrder( getAsStringOrNull( node.getChildOrder() ) );
        xml.setNodeType( node.getNodeType().getName() );

        if ( node.data() != null )
        {
            xml.setProperties( XmlPropertyTreeMapper.toXml( node.data() ) );
        }

        xml.setIndexConfigs( XmlIndexConfigsMapper.toXml( node.getIndexConfigDocument() ) );

        return xml;
    }

    private static String getAsStringOrNull( final Object object )
    {
        if ( object == null )
        {
            return null;
        }

        return object.toString();
    }

}
