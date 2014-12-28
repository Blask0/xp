package com.enonic.wem.repo.internal.elasticsearch.query;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.node.NodeIndexPath;
import com.enonic.wem.api.node.NodeQuery;
import com.enonic.wem.api.query.filter.ValueFilter;
import com.enonic.wem.api.security.PrincipalKeys;
import com.enonic.wem.repo.internal.elasticsearch.aggregation.query.AggregationQueryBuilderFactory;
import com.enonic.wem.repo.internal.elasticsearch.query.builder.AclFilterBuilderFactory;
import com.enonic.wem.repo.internal.elasticsearch.query.builder.FilterBuilderFactory;
import com.enonic.wem.repo.internal.elasticsearch.query.builder.QueryBuilderFactory;
import com.enonic.wem.repo.internal.elasticsearch.query.builder.SortQueryBuilderFactory;
import com.enonic.wem.repo.internal.index.IndexContext;
import com.enonic.wem.repo.internal.repository.IndexNameResolver;

public class NodeQueryTranslator
{

    public static ElasticsearchQuery translate( final NodeQuery nodeQuery, final IndexContext indexContext )
    {
        final QueryBuilder queryWithQueryFilters = createQueryWithQueryFilters( nodeQuery, indexContext );

        final ElasticsearchQuery.Builder queryBuilder = ElasticsearchQuery.create().
            index( IndexNameResolver.resolveSearchIndexName( indexContext.getRepositoryId() ) ).
            indexType( indexContext.getWorkspace().getName() ).
            query( queryWithQueryFilters ).
            setAggregations( AggregationQueryBuilderFactory.create( nodeQuery.getAggregationQueries() ) ).
            sortBuilders( SortQueryBuilderFactory.create( nodeQuery.getOrderBys() ) ).
            filter( FilterBuilderFactory.create( nodeQuery.getPostFilters() ) ).
            from( nodeQuery.getFrom() ).
            size( nodeQuery.getSize() );

        return queryBuilder.build();
    }

    private static QueryBuilder createQueryWithQueryFilters( final NodeQuery nodeQuery, final IndexContext context )
    {
        final QueryBuilderFactory.Builder queryBuilderBuilder = QueryBuilderFactory.create().
            queryExpr( nodeQuery.getQuery() ).
            addQueryFilters( nodeQuery.getQueryFilters() );

        addAclFilter( queryBuilderBuilder, context.getPrincipalKeys() );
        addParentFilter( nodeQuery, queryBuilderBuilder );
        addPathFilter( nodeQuery, queryBuilderBuilder );

        return queryBuilderBuilder.build();
    }

    private static void addAclFilter( final QueryBuilderFactory.Builder queryBuilderBuilder, final PrincipalKeys principalsKeys )
    {
        queryBuilderBuilder.addQueryFilter( AclFilterBuilderFactory.create( principalsKeys ) );
    }

    private static void addPathFilter( final NodeQuery nodeQuery, final QueryBuilderFactory.Builder queryBuilderBuilder )
    {
        if ( nodeQuery.getPath() != null )
        {
            queryBuilderBuilder.addQueryFilter( ValueFilter.create().
                fieldName( NodeIndexPath.PATH.getPath() ).
                addValue( Value.newString( nodeQuery.getPath().toString() ) ).
                build() );
        }
    }

    private static void addParentFilter( final NodeQuery nodeQuery, final QueryBuilderFactory.Builder queryBuilderBuilder )
    {
        if ( nodeQuery.getParent() != null )
        {
            queryBuilderBuilder.addQueryFilter( ValueFilter.create().
                fieldName( NodeIndexPath.PARENT_PATH.getPath() ).
                addValue( Value.newString( nodeQuery.getParent().toString() ) ).
                build() );
        }
    }


}
