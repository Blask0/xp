package com.enonic.wem.repo.internal.elasticsearch.version;

import java.time.Instant;

import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.enonic.wem.api.index.IndexPath;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeVersion;
import com.enonic.wem.api.node.NodeVersionId;
import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchDao;
import com.enonic.wem.repo.internal.elasticsearch.ReturnFields;
import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.repo.internal.index.IndexType;
import com.enonic.wem.repo.internal.index.result.SearchResult;
import com.enonic.wem.repo.internal.index.result.SearchResultEntry;
import com.enonic.wem.repo.internal.index.result.SearchResultFieldValue;
import com.enonic.wem.repo.internal.repository.StorageNameResolver;
import com.enonic.wem.repo.internal.version.VersionIndexPath;

class AbstractVersionsCommand
{
    final ElasticsearchDao elasticsearchDao;

    final RepositoryId repositoryId;

    AbstractVersionsCommand( Builder builder )
    {
        this.elasticsearchDao = builder.elasticsearchDao;
        this.repositoryId = builder.repositoryId;
    }

    SearchResult doGetFromNodeId( final NodeId id, final int from, final int size, final RepositoryId repositoryId )
    {
        final TermQueryBuilder nodeIdQuery = new TermQueryBuilder( VersionIndexPath.NODE_ID.getPath(), id.toString() );

        final ElasticsearchQuery query = ElasticsearchQuery.create().
            index( StorageNameResolver.resolveStorageIndexName( repositoryId ) ).
            indexType( IndexType.VERSION.getName() ).
            query( nodeIdQuery ).
            from( from ).
            size( size ).
            addSortBuilder( new FieldSortBuilder( VersionIndexPath.TIMESTAMP.getPath() ).order( SortOrder.DESC ) ).
            setReturnFields( ReturnFields.from( VersionIndexPath.TIMESTAMP, VersionIndexPath.VERSION_ID, VersionIndexPath.NODE_ID ) ).
            build();

        final SearchResult searchResults = elasticsearchDao.find( query );

        if ( searchResults.isEmpty() )
        {
            throw new RuntimeException( "Did not find version entry with id: " + id );
        }
        return searchResults;
    }

    NodeVersion createVersionEntry( final SearchResultEntry hit )
    {
        final String timestamp = getStringValue( hit, VersionIndexPath.TIMESTAMP, true );
        final String versionId = getStringValue( hit, VersionIndexPath.VERSION_ID, true );

        return new NodeVersion( NodeVersionId.from( versionId ), Instant.parse( timestamp ) );
    }

    private String getStringValue( final SearchResultEntry hit, final IndexPath indexPath, final boolean required )
    {
        final SearchResultFieldValue field = hit.getField( indexPath.getPath(), required );

        if ( field == null )
        {
            return null;
        }

        return field.getValue().toString();
    }

    static class Builder<B extends Builder>
    {
        private ElasticsearchDao elasticsearchDao;

        private RepositoryId repositoryId;

        Builder()
        {
        }

        @SuppressWarnings("unchecked")
        B elasticsearchDao( ElasticsearchDao elasticsearchDao )
        {
            this.elasticsearchDao = elasticsearchDao;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        B repositoryId( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return (B) this;
        }
    }
}
