package com.enonic.xp.repo.impl.dump.upgrade.flattenedpage;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;

import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageSourceConstants.SRC_COMPONENT_KEY;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageSourceConstants.SRC_CONFIG_KEY;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageSourceConstants.SRC_CONTROLLER_KEY;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageSourceConstants.SRC_CUSTOMIZED_KEY;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageSourceConstants.SRC_FRAGMENT_KEY;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageSourceConstants.SRC_NAME_KEY;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageSourceConstants.SRC_PAGE_KEY;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageSourceConstants.SRC_REGION_KEY;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageSourceConstants.SRC_TEMPLATE_KEY;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageSourceConstants.SRC_TEXT_KEY;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageSourceConstants.SRC_TYPE_KEY;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageSourceConstants.SRC_TYPE_VALUE;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageTargetConstants.TGT_CAPTION_KEY;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageTargetConstants.TGT_COMPONENTS_KEY;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageTargetConstants.TGT_CONFIG_KEY;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageTargetConstants.TGT_CUSTOMIZED_KEY;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageTargetConstants.TGT_DESCRIPTOR_KEY;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageTargetConstants.TGT_ID_KEY;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageTargetConstants.TGT_PAGE_PATH_VALUE;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageTargetConstants.TGT_PATH_KEY;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageTargetConstants.TGT_TEMPLATE_KEY;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageTargetConstants.TGT_TYPE_KEY;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageTargetConstants.TGT_TYPE_VALUE;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageTargetConstants.TGT_VALUE_KEY;


public class FlattenedPageDataUpgrader
{

    public boolean upgrade( final PropertyTree nodeData )
    {
        final PropertySet sourcePageSet = nodeData.getSet( SRC_PAGE_KEY );
        if ( sourcePageSet == null )
        {
            return false;
        }

        if ( isFragmentPage( sourcePageSet ) )
        {
            final PropertySet sourcePageFragmentSet = sourcePageSet.getPropertySet( SRC_FRAGMENT_KEY );
            addComponent( sourcePageFragmentSet, "/", nodeData );
        }
        else
        {
            addPageComponent( nodeData );
            addComponents( nodeData );
        }
        nodeData.removeProperty( FlattenedPageSourceConstants.SRC_PAGE_KEY );

        return true;
    }

    private boolean isFragmentPage( final PropertySet sourcePageSet )
    {
        return sourcePageSet.hasProperty( SRC_FRAGMENT_KEY );
    }

    private void addPageComponent( final PropertyTree nodeData )
    {
        final PropertySet pageComponentSet = nodeData.addSet( TGT_COMPONENTS_KEY );
        pageComponentSet.setString( TGT_TYPE_KEY, TGT_TYPE_VALUE.PAGE );
        pageComponentSet.setString( TGT_PATH_KEY, TGT_PAGE_PATH_VALUE );
        addPageComponentData( nodeData, pageComponentSet );
    }

    private void addPageComponentData( final PropertyTree nodeData, final PropertySet pageComponentSet )
    {
        final PropertySet pageComponentDataSet = pageComponentSet.addSet( TGT_TYPE_VALUE.PAGE );

        final PropertySet sourcePageSet = nodeData.getSet( SRC_PAGE_KEY );
        pageComponentDataSet.setString( TGT_DESCRIPTOR_KEY, sourcePageSet.getString( SRC_CONTROLLER_KEY ) );
        pageComponentDataSet.setReference( TGT_TEMPLATE_KEY, sourcePageSet.getReference( SRC_TEMPLATE_KEY ) );
        pageComponentDataSet.setBoolean( TGT_CUSTOMIZED_KEY, sourcePageSet.getBoolean( SRC_CUSTOMIZED_KEY ) );

        final PropertySet configSet = pageComponentDataSet.addSet( TGT_CONFIG_KEY );
        final String applicationKey = getApplicationKey( sourcePageSet.getString( SRC_CONTROLLER_KEY ) );
        configSet.setSet( applicationKey, sourcePageSet.getSet( SRC_CONFIG_KEY ) );
    }

    private String getApplicationKey( String descriptorKey )
    {
        return descriptorKey.split( ":" )[0];
    }

    private void addComponents( final PropertyTree nodeData )
    {
        final PropertySet sourcePageSet = nodeData.getSet( SRC_PAGE_KEY );

        addComponentsFromRegions( sourcePageSet, "/", nodeData );

    }

    private void addComponentsFromRegions( final PropertySet sourceComponentSet, final String path, final PropertyTree nodeData )
    {
        for ( PropertySet region : sourceComponentSet.getSets( SRC_REGION_KEY ) )
        {
            final String regionName = region.getString( SRC_NAME_KEY );

            int componentIndex = 0;
            for ( PropertySet sourceComponent : region.getSets( SRC_COMPONENT_KEY ) )
            {
                addComponent( sourceComponent, ( "/".equals( path ) ? "" : path ) + "/" + regionName + "/" + componentIndex, nodeData );
            }
        }
    }

    private void addComponent( final PropertySet sourceComponentSet, final String componentPath, final PropertyTree nodeData )
    {
        final PropertySet componentSet = nodeData.addSet( TGT_COMPONENTS_KEY );

        final String sourceType = sourceComponentSet.getString( SRC_TYPE_KEY );
        final String targetType = getTargetType( sourceType );
        componentSet.setString( TGT_TYPE_KEY, targetType );
        componentSet.setString( TGT_PATH_KEY, componentPath );

        final PropertySet sourceComponentDataSet = sourceComponentSet.getSet( sourceType );
        final PropertySet targetComponentDataSet = componentSet.addSet( targetType );

        switch ( sourceType )
        {
            case SRC_TYPE_VALUE.LAYOUT:
                addLayoutComponentData( sourceComponentDataSet, targetComponentDataSet, componentPath, nodeData );
                break;
            case SRC_TYPE_VALUE.IMAGE:
                addImageComponentData( sourceComponentDataSet, targetComponentDataSet );
                break;
            case SRC_TYPE_VALUE.PART:
                addPartComponentData( sourceComponentDataSet, targetComponentDataSet );
                break;
            case SRC_TYPE_VALUE.TEXT:
                addTextComponentData( sourceComponentDataSet, targetComponentDataSet );
                break;
            case SRC_TYPE_VALUE.FRAGMENT:
                addFragmentComponentData( sourceComponentDataSet, targetComponentDataSet );
                break;
        }
    }

    private void addTextComponentData( final PropertySet sourceComponentDataSet, final PropertySet targetComponentDataSet )
    {
        targetComponentDataSet.setString( TGT_VALUE_KEY, sourceComponentDataSet.getString( SRC_TEXT_KEY ) );
    }

    private void addFragmentComponentData( final PropertySet sourceComponentDataSet, final PropertySet targetComponentDataSet )
    {
        targetComponentDataSet.setReference( TGT_ID_KEY, sourceComponentDataSet.getReference( SRC_FRAGMENT_KEY ) );
    }

    private void addImageComponentData( final PropertySet sourceComponentDataSet, final PropertySet targetComponentDataSet )
    {
        targetComponentDataSet.setReference( TGT_ID_KEY, sourceComponentDataSet.getReference( SRC_FRAGMENT_KEY ) );
        targetComponentDataSet.setString( TGT_CAPTION_KEY, sourceComponentDataSet.getSet( SRC_CONFIG_KEY ).
            getString( "caption" ) );
    }

    private void addPartComponentData( final PropertySet sourceComponentDataSet, final PropertySet targetComponentDataSet )
    {
        addDescriptorBasedComponentData( sourceComponentDataSet, targetComponentDataSet );
    }

    private void addLayoutComponentData( final PropertySet sourceComponentDataSet, final PropertySet targetComponentDataSet,
                                         final String componentPath, final PropertyTree nodeData )
    {
        addDescriptorBasedComponentData( sourceComponentDataSet, targetComponentDataSet );
        addComponentsFromRegions( sourceComponentDataSet, componentPath, nodeData );
    }

    private void addDescriptorBasedComponentData( final PropertySet sourceComponentDataSet, final PropertySet targetComponentDataSet )
    {
        targetComponentDataSet.setString( TGT_DESCRIPTOR_KEY, sourceComponentDataSet.getString( SRC_TEMPLATE_KEY ) );
        final PropertySet targetConfigSet = targetComponentDataSet.addSet( TGT_CONFIG_KEY );
        final String applicationKey = getApplicationKey( sourceComponentDataSet.getString( SRC_TEMPLATE_KEY ) );
        targetConfigSet.setSet( applicationKey, sourceComponentDataSet.getSet( SRC_CONFIG_KEY ) );
    }

    private String getTargetType( final String sourceComponentType )
    {
        switch ( sourceComponentType )
        {
            case SRC_TYPE_VALUE.LAYOUT:
                return TGT_TYPE_VALUE.LAYOUT;
            case SRC_TYPE_VALUE.IMAGE:
                return TGT_TYPE_VALUE.IMAGE;
            case SRC_TYPE_VALUE.PART:
                return TGT_TYPE_VALUE.PART;
            case SRC_TYPE_VALUE.TEXT:
                return TGT_TYPE_VALUE.TEXT;
            case SRC_TYPE_VALUE.FRAGMENT:
                return TGT_TYPE_VALUE.FRAGMENT;
        }
        return null;
    }
}
