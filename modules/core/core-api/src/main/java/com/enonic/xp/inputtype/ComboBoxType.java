package com.enonic.xp.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

final class ComboBoxType
    extends InputTypeBase
{
    public final static ComboBoxType INSTANCE = new ComboBoxType();

    private ComboBoxType()
    {
        super( InputTypeName.COMBO_BOX );
    }

    @Override
    public Value createValue( final String value, final InputTypeConfig config )
    {
        return ValueFactory.newString( value );
    }

    @Override
    public Value createDefaultValue( final InputTypeConfig defaultConfig )
    {
        final InputTypeProperty defaultProperty = defaultConfig.getProperty( "default" );
        if ( defaultProperty != null )
        {
            return ValueFactory.newString( defaultProperty.getValue() );
        }
        return super.createDefaultValue( defaultConfig );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.STRING );

        final String valueAsString = property.getString();
        final boolean flag = ( valueAsString != null ) && config.hasAttributeValue( "option", "value", valueAsString );
        validateValue( property, flag, "Value is not a valid option" );
    }
}
