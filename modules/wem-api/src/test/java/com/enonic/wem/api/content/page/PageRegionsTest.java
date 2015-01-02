package com.enonic.wem.api.content.page;


import java.util.Iterator;

import org.junit.Test;

import com.google.common.collect.UnmodifiableIterator;

import com.enonic.wem.api.content.page.layout.LayoutComponent;
import com.enonic.wem.api.content.page.layout.LayoutRegions;
import com.enonic.wem.api.content.page.region.Region;

import static com.enonic.wem.api.content.page.PageRegions.newPageRegions;
import static com.enonic.wem.api.content.page.layout.LayoutComponent.newLayoutComponent;
import static com.enonic.wem.api.content.page.layout.LayoutRegions.newLayoutRegions;
import static com.enonic.wem.api.content.page.part.PartComponent.newPartComponent;
import static com.enonic.wem.api.content.page.region.Region.newRegion;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

public class PageRegionsTest
{
    @Test
    public void iterator()
    {
        PageRegions regions = newPageRegions().
            add( newRegion().name( "a-region" ).build() ).
            add( newRegion().name( "b-region" ).build() ).
            add( newRegion().name( "c-region" ).build() ).
            build();

        Iterator<Region> iterator = regions.iterator();
        Region nextRegion = iterator.next();
        assertNotNull( nextRegion );
        assertEquals( "a-region", nextRegion.getName() );

        nextRegion = iterator.next();
        assertNotNull( nextRegion );
        assertEquals( "b-region", nextRegion.getName() );

        nextRegion = iterator.next();
        assertNotNull( nextRegion );
        assertEquals( "c-region", nextRegion.getName() );

        assertFalse( iterator.hasNext() );
    }

    @Test
    public void getRegion()
    {
        PageRegions regions = newPageRegions().
            add( newRegion().name( "a-region" ).build() ).
            add( newRegion().name( "b-region" ).build() ).
            add( newRegion().name( "c-region" ).build() ).
            build();

        assertEquals( "a-region", regions.getRegion( "a-region" ).getName() );
        assertEquals( "b-region", regions.getRegion( "b-region" ).getName() );
        assertEquals( "c-region", regions.getRegion( "c-region" ).getName() );
        assertNull( regions.getRegion( "no-region" ) );
    }

    @Test
    public void componentPaths_one_level()
    {
        PageRegions regions = newPageRegions().
            add( newRegion().name( "a-region" ).
                add( newPartComponent().name( ComponentName.from( "part-a-in-a" ) ).build() ).
                build() ).
            add( newRegion().name( "b-region" ).
                add( newPartComponent().name( ComponentName.from( "part-a-in-b" ) ).build() ).
                add( newPartComponent().name( ComponentName.from( "part-b-in-b" ) ).build() ).
                build() ).
            build();

        Iterator<Region> iterator = regions.iterator();

        // verify: components in a-region
        Region nextRegion = iterator.next();
        UnmodifiableIterator<Component> components = nextRegion.getComponents().iterator();
        assertEquals( "a-region/0", components.next().getPath().toString() );

        // verify: components in b-region
        nextRegion = iterator.next();
        components = nextRegion.getComponents().iterator();
        assertEquals( "b-region/0", components.next().getPath().toString() );
        assertEquals( "b-region/1", components.next().getPath().toString() );
    }

    @Test
    public void componentPaths_two_levels()
    {
        PageRegions pageRegions = newPageRegions().
            add( newRegion().name( "region-level-1" ).
                add( newLayoutComponent().name( ComponentName.from( "layout-level-1" ) ).
                    regions( newLayoutRegions().
                        add( newRegion().name( "region-level-2" ).
                            add( newPartComponent().name( ComponentName.from( "part-level-2" ) ).build() ).
                            build() ).
                        build() ).
                    build() ).
                build() ).
            build();

        // verify
        Region regionLevel1 = pageRegions.iterator().next();
        UnmodifiableIterator<Component> componentsLevel1 = regionLevel1.getComponents().iterator();
        LayoutComponent layoutLevel1 = (LayoutComponent) componentsLevel1.next();
        assertEquals( "region-level-1/0", layoutLevel1.getPath().toString() );

        LayoutRegions layoutRegions = layoutLevel1.getRegions();
        Region regionLevel2 = layoutRegions.iterator().next();
        UnmodifiableIterator<Component> componentsLevel2 = regionLevel2.getComponents().iterator();
        assertEquals( "region-level-1/0/region-level-2/0", componentsLevel2.next().getPath().toString() );
    }

    @Test
    public void getComponent()
    {
        PageRegions regions = newPageRegions().
            add( newRegion().
                name( "a-region" ).
                add( newPartComponent().name( "part-1-in-region-a" ).build() ).
                build() ).
            add( newRegion().
                name( "b-region" ).
                add( newPartComponent().name( "part-1-in-region-b" ).build() ).
                build() ).
            build();

        assertEquals( "part-1-in-region-a", regions.getComponent( ComponentPath.from( "a-region/0" ) ).getName().toString() );
        assertEquals( "part-1-in-region-b", regions.getComponent( ComponentPath.from( "b-region/0" ) ).getName().toString() );
        assertNull( regions.getComponent( ComponentPath.from( "a-region/1" ) ) );
    }
}
