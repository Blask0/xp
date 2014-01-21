package com.enonic.wem.admin.json.content.page.layout;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.enonic.wem.admin.json.content.page.region.RegionJson;
import com.enonic.wem.api.content.page.layout.LayoutRegions;
import com.enonic.wem.api.content.page.region.Region;

import static com.enonic.wem.api.content.page.layout.LayoutRegions.newLayoutRegions;

@SuppressWarnings("UnusedDeclaration")
public class LayoutRegionsJson
{
    private final LayoutRegions regions;

    private final List<RegionJson> regionsJson;

    public LayoutRegionsJson( final LayoutRegions regions )
    {
        this.regions = regions;

        if ( regions != null )
        {
            regionsJson = new ArrayList<>();
            for ( Region region : regions )
            {
                regionsJson.add( new RegionJson( region ) );
            }
        }
        else
        {
            regionsJson = null;
        }
    }

    @JsonCreator
    public LayoutRegionsJson( final List<RegionJson> regionJsons )
    {
        this.regionsJson = regionJsons;
        final LayoutRegions.Builder builder = newLayoutRegions();
        for ( RegionJson region : regionJsons )
        {
            builder.add( region.getRegion() );
        }
        this.regions = builder.build();
    }

    public List<RegionJson> getRegions()
    {
        return regionsJson;
    }

    @JsonIgnore
    public LayoutRegions getLayoutRegions()
    {
        return regions;
    }
}
