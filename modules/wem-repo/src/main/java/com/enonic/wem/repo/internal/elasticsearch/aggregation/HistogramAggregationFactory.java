package com.enonic.wem.repo.internal.elasticsearch.aggregation;

import java.util.Collection;

import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;

import com.enonic.wem.api.aggregation.BucketAggregation;
import com.enonic.wem.api.aggregation.Buckets;

class HistogramAggregationFactory
    extends AggregationsFactory
{
    static BucketAggregation create( final Histogram histogram )
    {
        return BucketAggregation.bucketAggregation( histogram.getName() ).
            buckets( createBuckets( histogram.getBuckets() ) ).
            build();
    }

    private static Buckets createBuckets( final Collection<? extends Histogram.Bucket> buckets )
    {
        final Buckets.Builder bucketsBuilder = new Buckets.Builder();

        for ( final Histogram.Bucket bucket : buckets )
        {
            createAndAddBucket( bucketsBuilder, bucket );
        }

        return bucketsBuilder.build();
    }
}
