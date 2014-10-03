package com.enonic.wem.api.content.site;


import java.util.List;

import org.junit.Test;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleKey;

import static com.enonic.wem.api.content.site.ModuleConfig.newModuleConfig;
import static com.google.common.collect.Lists.newArrayList;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class SiteTest
{

    @Test
    public void EditBuilder_isChanges_given_values_with_no_changes_since_original_then_false_is_returned()
    {
        // setup
        Site original = Site.newSite().
            addModuleConfig( newModuleConfig().module( ModuleKey.from( "unchanged-1.1.1" ) ).config( new RootDataSet() ).build() ).
            addModuleConfig( newModuleConfig().module( ModuleKey.from( "unchanged-1.1.2" ) ).config( new RootDataSet() ).build() ).
            name( "my-site" ).
            parentPath( ContentPath.ROOT ).
            build();

        List<ModuleConfig> newConfigs =
            newArrayList( newModuleConfig().module( ModuleKey.from( "unchanged-1.1.1" ) ).config( new RootDataSet() ).build(),
                          newModuleConfig().module( ModuleKey.from( "unchanged-1.1.2" ) ).config( new RootDataSet() ).build() );

        Site.SiteEditBuilder editBuilder = Site.editSite( original ).
            moduleConfigs( ModuleConfigs.from( newConfigs ) );

        // exercise & verify
        assertFalse( editBuilder.isChanges() );
    }

    @Test
    public void EditBuilder_isChanges_given_no_then_false_is_returned()
    {
        // setup
        Site original = Site.newSite().
            addModuleConfig( newModuleConfig().module( ModuleKey.from( "unchanged-1.1.1" ) ).config( new RootDataSet() ).build() ).
            addModuleConfig( newModuleConfig().module( ModuleKey.from( "unchanged-1.1.2" ) ).config( new RootDataSet() ).build() ).
            name( "my-site" ).
            parentPath( ContentPath.ROOT ).
            build();

        List<ModuleConfig> newConfigs =
            newArrayList( newModuleConfig().module( ModuleKey.from( "unchanged-1.1.1" ) ).config( new RootDataSet() ).build(),
                          newModuleConfig().module( ModuleKey.from( "unchanged-1.1.2" ) ).config( new RootDataSet() ).build() );

        Site.SiteEditBuilder editBuilder = Site.editSite( original ).
            moduleConfigs( ModuleConfigs.from( newConfigs ) );

        // exercise & verify
        assertFalse( editBuilder.isChanges() );
    }

    @Test
    public void EditBuilder_isChanges_given_values_with_changes_in_module_config_since_original_then_true_is_returned()
    {
        // setup
        Site original = Site.newSite().
            addModuleConfig( newModuleConfig().module( ModuleKey.from( "unchanged-1.1.1" ) ).config( new RootDataSet() ).build() ).
            addModuleConfig( newModuleConfig().module( ModuleKey.from( "mymodule-1.1.2" ) ).config( new RootDataSet() ).build() ).
            name( "my-site" ).
            parentPath( ContentPath.ROOT ).
            build();

        List<ModuleConfig> newConfigs =
            newArrayList( newModuleConfig().module( ModuleKey.from( "unchanged-1.1.1" ) ).config( new RootDataSet() ).build(),
                          newModuleConfig().module( ModuleKey.from( "changed-1.1.2" ) ).config( new RootDataSet() ).build() );

        Site.SiteEditBuilder editBuilder = Site.editSite( original ).
            moduleConfigs( ModuleConfigs.from( newConfigs ) );

        // exercise & verify
        assertTrue( editBuilder.isChanges() );
    }
}
