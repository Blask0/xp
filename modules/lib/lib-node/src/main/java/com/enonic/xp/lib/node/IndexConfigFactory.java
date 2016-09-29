package com.enonic.xp.lib.node;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.PatternIndexConfigDocument;

import static com.enonic.xp.lib.node.NodePropertyConstants.ANALYZER;
import static com.enonic.xp.lib.node.NodePropertyConstants.CONFIG_ARRAY;
import static com.enonic.xp.lib.node.NodePropertyConstants.CONFIG_PATH;
import static com.enonic.xp.lib.node.NodePropertyConstants.CONFIG_SETTINGS;

public class IndexConfigFactory
{
    public static final IndexConfig DEFAULT_CONFIG = IndexConfig.BY_TYPE;

    private final PropertySet propertySet;

    public IndexConfigFactory( final PropertySet propertySet )
    {
        this.propertySet = propertySet;
    }

    public IndexConfigDocument create()
    {
        final PatternIndexConfigDocument.Builder builder = PatternIndexConfigDocument.create();
        builder.analyzer( this.propertySet.getString( ANALYZER ) );
        createDefaultSettings( builder );
        createPathConfigs( builder );
        return builder.build();
    }

    private void createDefaultSettings( final PatternIndexConfigDocument.Builder builder )
    {
        final Property defaultConfig = this.propertySet.getProperty( NodePropertyConstants.DEFAULT_CONFIG );

        if ( defaultConfig != null )
        {
            builder.defaultConfig( createConfig( defaultConfig ) );
        }
        else
        {
            builder.defaultConfig( DEFAULT_CONFIG );
        }
    }

    private void createPathConfigs( final PatternIndexConfigDocument.Builder builder )
    {
        final Iterable<PropertySet> pathConfigs = this.propertySet.getSets( CONFIG_ARRAY );

        if ( pathConfigs == null )
        {
            return;
        }

        for ( final PropertySet pathConfig : pathConfigs )
        {
            final String path = pathConfig.getString( CONFIG_PATH );
            final IndexConfig config = createConfig( pathConfig.getProperty( CONFIG_SETTINGS ) );
            builder.add( path, config );
        }
    }

    private IndexConfig createConfig( final Property defaultSettings )
    {
        if ( defaultSettings.getType().equals( ValueTypes.STRING ) )
        {
            return create( defaultSettings.getString() );
        }
        else if ( defaultSettings.getType().equals( ValueTypes.PROPERTY_SET ) )
        {
            return create( defaultSettings.getSet() );
        }
        else
        {
            throw new IllegalArgumentException( "Wrong format on indexConfig for default settings" );
        }
    }

    private IndexConfig create( final PropertySet settings )
    {
        final Boolean decideByType = settings.getBoolean( "decideByType" );
        final Boolean enabled = settings.getBoolean( "enabled" );
        final Boolean nGram = settings.getBoolean( "nGram" );
        final Boolean fulltext = settings.getBoolean( "fulltext" );
        final Boolean includeInAllText = settings.getBoolean( "includeInAllText" );

        final Iterable<String> indexValueProcessors = settings.getStrings( "indexValueProcessors" );

        final IndexConfig.Builder builder = IndexConfig.create().
            decideByType( decideByType ).
            enabled( enabled ).
            nGram( nGram ).
            fulltext( fulltext ).
            includeInAllText( includeInAllText );

        for ( final String indexValueProcessor : indexValueProcessors )
        {
            // TODO: How to inject these?
        }

        return builder.build();
    }

    private IndexConfig create( final String alias )
    {
        try
        {
            return IndexConfigAlias.from( alias );
        }
        catch ( Exception e )
        {
            throw new IllegalArgumentException( "Failed to parse alias [" + alias + "] from index config", e );
        }
    }

}
