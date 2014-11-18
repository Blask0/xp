package com.enonic.wem.repo.internal.elasticsearch;

import java.util.Collection;
import java.util.Set;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.index.IndexPaths;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeIds;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodePaths;
import com.enonic.wem.api.node.NodeQuery;
import com.enonic.wem.api.node.NodeVersionId;
import com.enonic.wem.api.node.NodeVersionIds;
import com.enonic.wem.api.query.QueryException;
import com.enonic.wem.api.query.expr.OrderExpressions;
import com.enonic.wem.api.query.filter.ValueFilter;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.repo.internal.elasticsearch.query.NodeQueryTranslator;
import com.enonic.wem.repo.internal.elasticsearch.query.builder.AclFilterBuilderFactory;
import com.enonic.wem.repo.internal.elasticsearch.query.builder.QueryBuilderFactory;
import com.enonic.wem.repo.internal.elasticsearch.query.builder.SortQueryBuilderFactory;
import com.enonic.wem.repo.internal.index.IndexContext;
import com.enonic.wem.repo.internal.index.query.NodeQueryResult;
import com.enonic.wem.repo.internal.index.query.QueryResultFactory;
import com.enonic.wem.repo.internal.index.query.QueryService;
import com.enonic.wem.repo.internal.index.result.GetResult;
import com.enonic.wem.repo.internal.index.result.SearchResult;
import com.enonic.wem.repo.internal.index.result.SearchResultEntry;
import com.enonic.wem.repo.internal.index.result.SearchResultFieldValue;
import com.enonic.wem.repo.internal.repository.IndexNameResolver;

public class ElasticsearchQueryService
    implements QueryService
{
    private ElasticsearchDao elasticsearchDao;

    private final QueryResultFactory queryResultFactory = new QueryResultFactory();

    @Override
    public NodeQueryResult find( final NodeQuery query, final IndexContext context )
    {
        final ElasticsearchQuery esQuery = NodeQueryTranslator.translate( query, context );

        //System.out.println( esQuery );

        return doFind( esQuery );
    }

    private NodeQueryResult doFind( final ElasticsearchQuery query )
    {
        final SearchResult searchResult = elasticsearchDao.find( query );

        return translateResult( searchResult );
    }

    public NodeVersionId get( final NodeId nodeId, final IndexContext indexContext )
    {
        final GetResult result = elasticsearchDao.get( GetQuery.create().
            indexName( IndexNameResolver.resolveSearchIndexName( indexContext.getRepositoryId() ) ).
            indexTypeName( indexContext.getWorkspace().getName() ).
            returnFields( ReturnFields.from( IndexPaths.VERSION_KEY, IndexPaths.HAS_READ_KEY ) ).
            id( nodeId ).
            build() );

        if ( result.isEmpty() )
        {
            return null;
        }

        if ( !GetResultCanReadResolver.canRead( indexContext.getPrincipalKeys(), result ) )
        {
            return null;
        }

        final SearchResultFieldValue nodeVersionId = result.getSearchResult().getField( IndexPaths.VERSION_KEY );

        if ( nodeVersionId == null )
        {
            throw new QueryException( "Expected field " + IndexPaths.VERSION_KEY + " not found in search result for nodeId " + nodeId );
        }

        return NodeVersionId.from( nodeVersionId.getValue().toString() );
    }


    @Override
    public NodeVersionId get( final NodePath nodePath, final IndexContext indexContext )
    {
        final Workspace workspace = indexContext.getWorkspace();

        final QueryBuilder queryBuilder = QueryBuilderFactory.create().
            addQueryFilter( AclFilterBuilderFactory.create( indexContext.getPrincipalKeys() ) ).
            addQueryFilter( ValueFilter.create().
                fieldName( IndexPaths.PATH_KEY ).
                addValue( Value.newString( nodePath.toString() ) ).
                build() ).
            build();

        final ElasticsearchQuery query = ElasticsearchQuery.create().
            index( IndexNameResolver.resolveSearchIndexName( indexContext.getRepositoryId() ) ).
            indexType( workspace.getName() ).
            query( queryBuilder ).
            size( 1 ).
            setReturnFields( ReturnFields.from( IndexPaths.VERSION_KEY ) ).
            build();

        final SearchResult searchResult = elasticsearchDao.find( query );

        if ( searchResult.isEmpty() )
        {
            return null;
        }

        if ( searchResult.getResults().getTotalHits() > 1 )
        {
            throw new QueryException( "Expected at most 1 hit, found " + searchResult.getResults().getTotalHits() );
        }

        final SearchResultEntry firstHit = searchResult.getResults().getFirstHit();

        final SearchResultFieldValue versionKeyField = firstHit.getField( IndexPaths.VERSION_KEY );

        if ( versionKeyField == null )
        {
            throw new ElasticsearchDataException( "Field " + IndexPaths.VERSION_KEY + " not found on node with path " +
                                                      nodePath + " in workspace " + workspace );
        }

        return NodeVersionId.from( versionKeyField.getValue().toString() );
    }

    @Override
    public NodeVersionIds find( final NodePaths nodePaths, final OrderExpressions orderExprs, final IndexContext indexContext )
    {
        if ( nodePaths.isEmpty() )
        {
            return NodeVersionIds.empty();
        }

        final Workspace workspace = indexContext.getWorkspace();

        final QueryBuilder queryBuilder = QueryBuilderFactory.create().
            addQueryFilter( AclFilterBuilderFactory.create( indexContext.getPrincipalKeys() ) ).
            addQueryFilter( ValueFilter.create().
                fieldName( IndexPaths.PATH_KEY ).
                addValues( nodePaths.getAsStrings() ).
                build() ).
            build();

        final ElasticsearchQuery query = ElasticsearchQuery.create().
            index( IndexNameResolver.resolveSearchIndexName( indexContext.getRepositoryId() ) ).
            indexType( workspace.getName() ).
            query( queryBuilder ).
            sortBuilders( SortQueryBuilderFactory.create( orderExprs ) ).
            setReturnFields( ReturnFields.from( IndexPaths.VERSION_KEY ) ).
            size( nodePaths.getSize() ).
            build();

        final SearchResult searchResult = elasticsearchDao.find( query );

        if ( searchResult.isEmpty() )
        {
            return NodeVersionIds.empty();
        }

        final Set<SearchResultFieldValue> fieldValues = searchResult.getResults().getFields( IndexPaths.VERSION_KEY );

        return fieldValuesToVersionIds( fieldValues );
    }

    @Override
    public NodeVersionIds find( final NodeIds nodeIds, final OrderExpressions orderExprs, final IndexContext indexContext )
    {
        return doGetByIds( nodeIds, orderExprs, indexContext );
    }

    private NodeVersionIds doGetByIds( final NodeIds nodeIds, final OrderExpressions orderExprs, final IndexContext indexContext )
    {
        if ( nodeIds.isEmpty() )
        {
            return NodeVersionIds.empty();
        }

        final Workspace workspace = indexContext.getWorkspace();

        final QueryBuilder queryBuilder = QueryBuilderFactory.create().
            addQueryFilter( AclFilterBuilderFactory.create( indexContext.getPrincipalKeys() ) ).
            addQueryFilter( ValueFilter.create().
                fieldName( IndexPaths.ID_KEY ).
                addValues( nodeIds.getAsStrings() ).
                build() ).
            build();

        final ElasticsearchQuery query = ElasticsearchQuery.create().
            index( IndexNameResolver.resolveSearchIndexName( indexContext.getRepositoryId() ) ).
            indexType( workspace.getName() ).
            query( queryBuilder ).
            sortBuilders( SortQueryBuilderFactory.create( orderExprs ) ).
            setReturnFields( ReturnFields.from( IndexPaths.VERSION_KEY ) ).
            size( nodeIds.getSize() ).
            build();

        final SearchResult searchResult = elasticsearchDao.find( query );

        if ( searchResult.isEmpty() )
        {
            NodeVersionIds.empty();
        }

        final Set<SearchResultFieldValue> fieldValues = searchResult.getResults().getFields( IndexPaths.VERSION_KEY );

        return fieldValuesToVersionIds( fieldValues );
    }

    @Override
    public boolean hasChildren( final NodePath parentPath, final IndexContext indexContext )
    {
        final Context context = ContextAccessor.current();

        final QueryBuilder queryBuilder = QueryBuilderFactory.create().
            addQueryFilter( AclFilterBuilderFactory.create( indexContext.getPrincipalKeys() ) ).
            addQueryFilter( ValueFilter.create().
                fieldName( IndexPaths.PARENT_PATH_KEY ).
                addValue( Value.newString( parentPath.toString() ) ).
                build() ).
            build();

        final ElasticsearchQuery query = ElasticsearchQuery.create().
            index( IndexNameResolver.resolveSearchIndexName( context.getRepositoryId() ) ).
            indexType( context.getWorkspace().getName() ).
            query( queryBuilder ).
            build();

        final long count = elasticsearchDao.count( query );

        return count > 0;
    }

    private NodeVersionIds fieldValuesToVersionIds( final Collection<SearchResultFieldValue> fieldValues )
    {
        final NodeVersionIds.Builder builder = NodeVersionIds.create();

        for ( final SearchResultFieldValue searchResultFieldValue : fieldValues )
        {
            if ( searchResultFieldValue == null )
            {
                continue;
            }

            builder.add( NodeVersionId.from( searchResultFieldValue.getValue().toString() ) );
        }
        return builder.build();
    }


    private NodeQueryResult translateResult( final SearchResult searchResult )
    {
        return queryResultFactory.create( searchResult );
    }

    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }
}
