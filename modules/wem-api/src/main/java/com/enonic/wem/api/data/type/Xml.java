package com.enonic.wem.api.data.type;


import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;

public class Xml
    extends ValueType<java.lang.String>
{
    Xml( int key )
    {
        super( key, JavaTypeConverters.STRING );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.Xml( convert( value ) );
    }

    @Override
    public Property newProperty( final java.lang.String name, final Value value )
    {
        return new Property.Xml( name, value );
    }
}
