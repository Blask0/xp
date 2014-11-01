package com.enonic.wem.servlet.internal.exception;

import java.util.Stack;

public final class HtmlBuilder
{
    private final StringBuilder str;

    private final Stack<String> openTags;

    private boolean addedInner;

    public HtmlBuilder()
    {
        this.str = new StringBuilder();
        this.openTags = new Stack<>();
        this.addedInner = false;
    }

    private void closeIfNeeded()
    {
        if ( !this.addedInner && !this.openTags.isEmpty() )
        {
            this.str.append( '>' );
        }
    }

    public HtmlBuilder open( final String name )
    {
        closeIfNeeded();

        this.str.append( '<' );
        this.str.append( name );

        this.openTags.push( name );
        this.addedInner = false;

        return this;
    }

    public HtmlBuilder close()
    {
        this.str.append( "</" );
        this.str.append( this.openTags.pop() );
        this.str.append( '>' );
        return this;
    }

    public HtmlBuilder closeEmpty()
    {
        this.str.append( "/>" );
        this.openTags.pop();
        return this;
    }

    public HtmlBuilder attribute( final String name, final String value )
    {
        this.str.append( ' ' );
        this.str.append( name );
        this.str.append( "=\"" );
        this.str.append( value );
        this.str.append( '"' );
        this.addedInner = false;
        return this;
    }

    public HtmlBuilder text( final String text )
    {
        closeIfNeeded();
        this.str.append( text );
        this.addedInner = true;
        return this;
    }

    @Override
    public String toString()
    {
        return this.str.toString();
    }
}
