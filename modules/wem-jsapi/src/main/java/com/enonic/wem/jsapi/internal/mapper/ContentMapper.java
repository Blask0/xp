package com.enonic.wem.jsapi.internal.mapper;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Metadata;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.script.serializer.MapGenerator;
import com.enonic.wem.script.serializer.MapSerializable;

public final class ContentMapper
    implements MapSerializable
{
    private final Content value;

    public ContentMapper( final Content value )
    {
        this.value = value;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        serialize( gen, this.value );
    }

    private static void serialize( final MapGenerator gen, final Content value )
    {
        gen.value( "_id", value.getId() );
        gen.value( "_name", value.getName() );
        gen.value( "_path", value.getPath() );
        gen.value( "_creator", value.getCreator() );
        gen.value( "_modifier", value.getModifier() );
        gen.value( "_createdTime", value.getCreatedTime() );
        gen.value( "_modifiedTime", value.getModifiedTime() );
        gen.value( "type", value.getType() );
        gen.value( "displayName", value.getDisplayName() );

        serializeData( gen, value.getData() );
        serializeMetaData( gen, value.getAllMetadata() );
        serializePage( gen, value.getPage() );
    }

    private static void serializeData( final MapGenerator gen, final PropertyTree value )
    {
        gen.map( "data" );
        new PropertyTreeMapper( value ).serialize( gen );
        gen.end();
    }

    private static void serializeMetaData( final MapGenerator gen, final Iterable<Metadata> values )
    {
        gen.map( "metadata" );
        for ( final Metadata value : values )
        {
            gen.map( value.getName().toString() );
            new PropertyTreeMapper( value.getData() ).serialize( gen );
            gen.end();
        }
        gen.end();
    }

    private static void serializePage( final MapGenerator gen, final Page value )
    {
        gen.map( "page" );
        new PageMapper( value ).serialize( gen );
        gen.end();
    }
}
