package com.enonic.wem.api.query.aggregation;

public class DateHistogramAggregationQuery
    extends AbstractHistogramAggregationQuery<DateInterval>
{
    private final String format;

    private DateHistogramAggregationQuery( final Builder builder )
    {
        super( builder, builder.interval );
        this.format = builder.format;
    }

    public String getFormat()
    {
        return format;
    }

    public static Builder create( final String name )
    {
        return new Builder( name );
    }

    public static class Builder
        extends AbstractHistogramAggregationQuery.Builder<Builder, DateInterval>
    {
        private String format;

        public Builder( final String name )
        {
            super( name );
        }

        public Builder format( final String format )
        {
            this.format = format;
            return this;
        }

        public DateHistogramAggregationQuery build()
        {
            return new DateHistogramAggregationQuery( this );
        }
    }


}
