package com.enonic.wem.script.convert;

final class FloatConverter
    extends NumberConverter<Float>
{
    public FloatConverter()
    {
        super( Float.class );
    }

    @Override
    protected Float fromNumber( final Number value )
    {
        return value.floatValue();
    }

    @Override
    protected Float parse( final String value )
    {
        return Float.parseFloat( value );
    }
}
