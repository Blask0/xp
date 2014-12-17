package com.enonic.wem.api.query.aggregation;

public class TermsAggregationQuery
    extends AggregationQuery
{
    public final static int TERM_DEFAULT_SIZE = 10;

    private final String fieldName;

    private final int size;

    private final Direction orderDirection;

    private final Type orderType;

    private TermsAggregationQuery( final Builder builder )
    {
        super( builder );
        this.fieldName = builder.fieldName;
        this.size = builder.size;
        this.orderDirection = builder.direction;
        this.orderType = builder.type;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public int getSize()
    {
        return size;
    }

    public Direction getOrderDirection()
    {
        return orderDirection;
    }

    public Type getOrderType()
    {
        return orderType;
    }

    public static Builder create( final String name )
    {
        return new Builder( name );
    }

    public static class Builder
        extends AggregationQuery.Builder<Builder>
    {
        private Direction direction = Direction.ASC;

        private Type type = Type.TERM;

        private String fieldName;

        public Builder( final String name )
        {
            super( name );
        }

        private int size = TERM_DEFAULT_SIZE;

        public Builder fieldName( final String fieldName )
        {
            this.fieldName = fieldName;
            return this;
        }

        public Builder size( final Integer size )
        {
            this.size = size;
            return this;
        }

        public TermsAggregationQuery build()
        {
            return new TermsAggregationQuery( this );
        }

        public Builder orderDirection( final Direction direction )
        {
            this.direction = direction;
            return this;
        }

        public Builder orderType( final Type type )
        {
            this.type = type;
            return this;
        }
    }

    public enum Direction
    {
        ASC,
        DESC
    }

    public enum Type
    {
        TERM,
        DOC_COUNT
    }


}
