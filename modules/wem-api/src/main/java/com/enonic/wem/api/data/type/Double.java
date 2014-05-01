package com.enonic.wem.api.data.type;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;

public class Double
    extends ValueType<java.lang.Double>
{
    Double( int key )
    {
        super( key, JavaTypeConverters.DOUBLE );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.Double( convert( value ) );
    }

    @Override
    public Property newProperty( final java.lang.String name, final Value value )
    {
        return new Property.Double( name, value );
    }
}
