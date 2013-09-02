package com.enonic.wem.api.query;

public final class QueryException
    extends RuntimeException
{
    public QueryException( final String message, final Object... args )
    {
        super( String.format( message, args ) );
    }
}
