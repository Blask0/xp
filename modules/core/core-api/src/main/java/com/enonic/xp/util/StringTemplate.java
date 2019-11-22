package com.enonic.xp.util;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.io.Resources;

public final class StringTemplate
{
    private final String template;

    private static final Interpolator STRING_TEMPLATE_INTERPOLATOR = new Interpolator( "{{", "}}", '\\' );

    private static final Interpolator CLASSIC_INTERPOLATOR = new Interpolator( "${", "}", '$' );

    public StringTemplate( final String template )
    {
        this.template = template;
    }

    public String apply( final Map<String, String> model )
    {
        return STRING_TEMPLATE_INTERPOLATOR.interpolate( this.template, model::get, List.of() );
    }

    public static String interpolate( CharSequence sequence, Map<String, String> values )
    {
        return CLASSIC_INTERPOLATOR.interpolate( sequence, values::get, List.of() );
    }

    public static String interpolate( CharSequence sequence, Function<String, String> values )
    {
        return CLASSIC_INTERPOLATOR.interpolate( sequence, values, List.of() );
    }

    public static StringTemplate load( final Class context, final String name )
    {
        final URL url = context.getResource( name );
        if ( url == null )
        {
            throw new IllegalArgumentException( "Could not find resource [" + name + "]" );
        }

        try
        {
            final String value = Resources.toString( url, StandardCharsets.UTF_8 );
            return new StringTemplate( value );
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    private static class Interpolator
    {
        private final Pattern pattern;

        Interpolator( final String prefix, final String suffix, char escape )
        {
            pattern = Pattern.compile(
                "(?<escape>" + Pattern.quote( String.valueOf( escape ) ) + ")?" + "(?<placeholder>" + Pattern.quote( prefix ) +
                    "(?<variableName>[\\w.]+)" + Pattern.quote( suffix ) + ")" );
        }

        private String interpolate( CharSequence sequence, Function<String, String> values, List<String> interpolated )
        {
            final StringBuilder builder = new StringBuilder();
            final Matcher matcher = pattern.matcher( sequence );
            while ( matcher.find() )
            {
                final String escape = matcher.group( "escape" );
                final String replacement;
                if ( escape == null )
                {
                    final String variableName = matcher.group( "variableName" );
                    final String resolvedVariable = values.apply( variableName );
                    if ( resolvedVariable != null )
                    {
                        final List<String> newInterpolated = new ArrayList<>( interpolated );
                        if ( newInterpolated.contains( variableName ) )
                        {
                            throw new IllegalStateException( "Cyclic interpolation for " + variableName );
                        }
                        newInterpolated.add( variableName );

                        replacement = interpolate( resolvedVariable, values, newInterpolated );
                    }
                    else
                    {
                        replacement = matcher.group();
                    }
                }
                else
                {
                    replacement = matcher.group( "placeholder" );
                }

                matcher.appendReplacement( builder, Matcher.quoteReplacement( replacement ) );
            }
            return matcher.appendTail( builder ).toString();
        }
    }
}
