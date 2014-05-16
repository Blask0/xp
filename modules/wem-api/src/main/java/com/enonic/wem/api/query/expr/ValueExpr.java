package com.enonic.wem.api.query.expr;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.data.type.ValueType;
import com.enonic.wem.api.data.type.ValueTypes;

public final class ValueExpr
    implements Expression
{
    private final Value value;

    private ValueExpr( final Value value )
    {
        this.value = value;
    }

    public Value getValue()
    {
        return this.value;
    }

    @Override
    public String toString()
    {
        final ValueType type = this.value.getType();
        if ( type == ValueTypes.DOUBLE )
        {
            return this.value.asString();
        }

        if ( type == ValueTypes.DATE_TIME )
        {
            return typecastFunction( "instant", this.value.asString() );
        }

        if ( type == ValueTypes.GEO_POINT )
        {
            return typecastFunction( "geoPoint", this.value.asString() );
        }

        return quoteString( this.value.asString() );
    }

    private String typecastFunction( final String name, final String argument )
    {
        return name + "(" + quoteString( argument ) + ")";
    }

    private String quoteString( final String value )
    {
        if ( value.contains( "'" ) )
        {
            return "\"" + value + "\"";
        }
        else
        {
            return "'" + value + "'";
        }
    }

    public static ValueExpr string( final String value )
    {
        return new ValueExpr( Value.newString( value ) );
    }

    public static ValueExpr number( final Number value )
    {
        return new ValueExpr( Value.newDouble( value.doubleValue() ) );
    }

    public static ValueExpr instant( final String value )
    {
        return new ValueExpr( Value.newInstant( value ) );
    }

    public static ValueExpr geoPoint( final String value )
    {
        return new ValueExpr( Value.newGeoPoint( value ) );
    }
}
