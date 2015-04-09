package com.enonic.xp.form;


import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;

@Beta
public class ValidationRegex
{
    private Pattern pattern;

    private String string;

    public ValidationRegex( final String pattern )
    {
        this.pattern = Pattern.compile( pattern );
        this.string = pattern;
    }

    public void checkValidity( final Property property )
        throws BreaksRegexValidationException
    {
        if ( !( property.getValue().getType().getJavaType().equals( java.lang.String.class ) ) )
        {
            return;
        }

        final Matcher matcher = pattern.matcher( property.getString() );
        if ( !matcher.matches() )
        {
            throw new BreaksRegexValidationException( property, pattern.toString() );
        }
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final ValidationRegex that = (ValidationRegex) o;
        return Objects.equals( this.string, that.string );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.string );
    }

    @Override
    public String toString()
    {
        return string;
    }
}
