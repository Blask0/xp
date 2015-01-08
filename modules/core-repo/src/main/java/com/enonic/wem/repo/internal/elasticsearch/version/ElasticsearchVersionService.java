package com.enonic.wem.repo.internal.elasticsearch.version;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

import com.enonic.wem.api.node.FindNodeVersionsResult;
import com.enonic.wem.api.node.NodeVersion;
import com.enonic.wem.api.node.NodeVersionDiffQuery;
import com.enonic.wem.api.node.NodeVersionDiffResult;
import com.enonic.wem.api.node.NodeVersionId;
import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchDao;
import com.enonic.wem.repo.internal.elasticsearch.xcontent.VersionXContentBuilderFactory;
import com.enonic.wem.repo.internal.index.IndexType;
import com.enonic.wem.repo.internal.repository.StorageNameResolver;
import com.enonic.wem.repo.internal.version.GetVersionsQuery;
import com.enonic.wem.repo.internal.version.NodeVersionDocument;
import com.enonic.wem.repo.internal.version.NodeVersionDocumentId;
import com.enonic.wem.repo.internal.version.VersionService;

public class ElasticsearchVersionService
    implements VersionService
{
    private static final boolean DEFAULT_REFRESH = true;

    private ElasticsearchDao elasticsearchDao;

    @Override
    public void store( final NodeVersionDocument nodeVersionDocument, final RepositoryId repositoryId )
    {
        final IndexRequest versionsDocument = Requests.indexRequest().
            index( StorageNameResolver.resolveStorageIndexName( repositoryId ) ).
            type( IndexType.VERSION.getName() ).
            source( VersionXContentBuilderFactory.create( nodeVersionDocument ) ).
            id( new NodeVersionDocumentId( nodeVersionDocument.getNodeId(), nodeVersionDocument.getNodeVersionId() ).toString() ).
            refresh( DEFAULT_REFRESH );

        elasticsearchDao.store( versionsDocument );
    }

    @Override
    public NodeVersion getVersion( final NodeVersionId nodeVersionId, final RepositoryId repositoryId )
    {
        return GetVersionCommand.create().
            elasticsearchDao( this.elasticsearchDao ).
            repositoryId( repositoryId ).
            nodeVersionId( nodeVersionId ).
            build().
            execute();
    }

    @Override
    public FindNodeVersionsResult findVersions( final GetVersionsQuery query, final RepositoryId repositoryId )
    {
        return FindVersionsCommand.create().
            elasticsearchDao( this.elasticsearchDao ).
            repositoryId( repositoryId ).
            query( query ).
            build().
            execute();
    }

    @Override
    public NodeVersionDiffResult diff( final NodeVersionDiffQuery query, final RepositoryId repositoryId )
    {
        return NodeVersionDiffCommand.create().
            elasticsearchDao( this.elasticsearchDao ).
            repositoryId( repositoryId ).
            query( query ).
            build().
            execute();
    }

    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }
}
