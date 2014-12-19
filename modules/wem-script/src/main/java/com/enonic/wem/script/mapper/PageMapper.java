package com.enonic.wem.script.mapper;

import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.script.serializer.MapGenerator;
import com.enonic.wem.script.serializer.MapSerializable;

public final class PageMapper
    implements MapSerializable
{
    private final Page value;

    public PageMapper( final Page value )
    {
        this.value = value;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        serialize( gen, this.value );
    }

    private static void serialize( final MapGenerator gen, final Page value )
    {
        gen.value( "template", value.getTemplate() );
        gen.value( "controller", value.getController() );

        gen.map( "config" );
        new PropertyTreeMapper( value.getConfig() ).serialize( gen );
        gen.end();

        serializeRegions( gen, value.getRegions() );
    }

    private static void serializeRegions( final MapGenerator gen, final PageRegions values )
    {
        gen.array( "regions" );
        for ( final Region region : values )
        {
            gen.map();
            new RegionMapper( region ).serialize( gen );
            gen.end();
        }
        gen.end();
    }
}
