package com.enonic.wem.export.internal.xml.mapper;

import com.enonic.wem.api.index.PathIndexConfig;
import com.enonic.wem.export.internal.xml.XmlPathIndexConfig;

class XmlPathIndexConfigMapper
{
    static XmlPathIndexConfig toXml( final PathIndexConfig pathIndexConfig )
    {
        final XmlPathIndexConfig xmlPathIndexConfig = new XmlPathIndexConfig();

        xmlPathIndexConfig.setPath( pathIndexConfig.getPath().toString() );
        xmlPathIndexConfig.setIndexConfig( XmlIndexConfigMapper.toXml( pathIndexConfig.getIndexConfig() ) );

        return xmlPathIndexConfig;
    }
}
