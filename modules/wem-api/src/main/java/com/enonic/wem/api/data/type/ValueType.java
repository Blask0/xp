package com.enonic.wem.api.data.type;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.form.InvalidValueException;

/**
 * ValueTypes should only be created when:
 * * the type can give something more when indexed
 * * needs validation.
 */
public abstract class ValueType<T>
{
    private final int key;

    private final String name;

    private final Class classType;

    private final JavaTypeConverter<T> javaTypeConverter;

    public ValueType( final int key, final String name, final JavaTypeConverter<T> javaTypeConverter )
    {
        this.key = key;
        this.name = name;
        this.classType = javaTypeConverter.getClass();
        this.javaTypeConverter = javaTypeConverter;
    }

    public int getKey()
    {
        return key;
    }

    public java.lang.String getName()
    {
        return name;
    }

    public Class getClassType()
    {
        return classType;
    }

    public JavaTypeConverter getJavaTypeConverter()
    {
        return this.javaTypeConverter;
    }

    /**
     * Checks by default if given property's value is of correct Java class.
     * Can be overridden by concrete classes to do extensive validation.
     *
     * @param value the value to check the validity of
     * @throws ValueOfUnexpectedClassException
     * @throws InvalidValueException
     */
    public void checkValidity( final Value value )
        throws ValueOfUnexpectedClassException, InvalidValueException
    {
        checkValueIsOfExpectedClass( value );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof ValueType ) )
        {
            return false;
        }

        final ValueType that = (ValueType) o;
        return key == that.key;
    }

    @Override
    public int hashCode()
    {
        return key;
    }

    @Override
    public java.lang.String toString()
    {
        return name;
    }

    public boolean isObjectOfExpectedClass( final Object object )
    {
        Preconditions.checkNotNull( object, "Cannot check the type of a object that is null" );
        return javaTypeConverter.isInstance( object );
    }

    public boolean isValueOfExpectedClass( final Value value )
    {
        Preconditions.checkNotNull( value, "Cannot check the type of a value that is null" );
        return javaTypeConverter.isInstance( value.getObject() );
    }

    public void checkValueIsOfExpectedClass( final Value value )
        throws ValueOfUnexpectedClassException
    {
        if ( !isValueOfExpectedClass( value ) )
        {
            throw new ValueOfUnexpectedClassException( javaTypeConverter, value );
        }
    }

    /**
     * Attempts to convert given object to this type.
     */
    public T convert( final Object object )
    {
        return javaTypeConverter.convertFrom( object );
    }

    /**
     * Attempts to convert given java.lang.String to this type.
     */
    public T convert( final java.lang.String object )
    {
        return javaTypeConverter.convertFromString( object );
    }

    public Property newProperty( final java.lang.String name, final Object valueObj )
    {
        final Value value = newValue( valueObj );
        return newProperty( name, value );
    }

    public abstract Value newValue( Object value );

    public final Property newProperty( final java.lang.String name, final Value value )
    {
        return Property.newProperty( name, value );
    }
}
