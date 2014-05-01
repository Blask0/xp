package com.enonic.wem.api.data.type;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;

public class ContentId
    extends ValueType<com.enonic.wem.api.content.ContentId>
{
    ContentId( int key )
    {
        super( key, JavaTypeConverters.CONTENT_ID );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.ContentId( convert( value ) );
    }

    @Override
    public Property newProperty( final java.lang.String name, final Value value )
    {
        return new Property.ContentId( name, value );
    }
}
