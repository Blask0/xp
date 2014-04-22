package com.enonic.wem.api.content.site;

import javax.xml.bind.annotation.XmlElement;

import com.enonic.wem.api.xml.XmlObject;

public final class VendorXml
    implements XmlObject<Vendor, Vendor.Builder>
{
    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "url")
    private String url;

    @Override
    public void from( final Vendor vendor )
    {
        this.name = vendor.getName();
        this.url = vendor.getUrl();
    }

    @Override
    public void to( final Vendor.Builder vendorBuilder )
    {
        vendorBuilder.name( this.name );
        vendorBuilder.url( this.url );
    }
}
