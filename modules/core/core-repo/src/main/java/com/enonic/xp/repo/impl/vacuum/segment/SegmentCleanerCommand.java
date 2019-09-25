package com.enonic.xp.repo.impl.vacuum.segment;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.filter.RangeFilter;
import com.enonic.xp.repo.impl.node.LoadNodeVersionCommand;
import com.enonic.xp.repo.impl.vacuum.VacuumTaskParams;
import com.enonic.xp.repo.impl.version.VersionIndexPath;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;
import com.enonic.xp.repository.RepositorySegmentUtils;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.vacuum.VacuumTaskResult;

public class SegmentCleanerCommand
{
    private static final Logger LOG = LoggerFactory.getLogger( SegmentCleanerCommand.class );

    private RepositoryIds BUILTIN_REPOSITORIES = RepositoryIds.from( SystemConstants.SYSTEM_REPO_ID, ContentConstants.CONTENT_REPO_ID );

    private RepositoryService repositoryService;

    private NodeService nodeService;

    private BlobStore blobStore;

    private final VacuumTaskParams params;

    private VacuumTaskResult.Builder result;

    private HashMap<RepositoryId, Boolean> repositoryPresenceMap;

    private SegmentCleanerCommand( final Builder builder )
    {
        blobStore = builder.blobStore;
        repositoryService = builder.repositoryService;
        nodeService = builder.nodeService;
        params = builder.params;
        result = VacuumTaskResult.create().
            taskName( builder.name );
        repositoryPresenceMap = Maps.newHashMap();
        generateRepositoryPresenceMap();
    }

    private void generateRepositoryPresenceMap()
    {
        BUILTIN_REPOSITORIES.forEach( repositoryId -> repositoryPresenceMap.put( repositoryId, true ) );
        repositoryService.list().forEach( repository -> repositoryPresenceMap.put( repository.getId(), true ) );
    }

    public VacuumTaskResult execute()
    {
        LOG.info( "Traversing segments....." );

        List<Segment> toBeRemoved = new ArrayList<>();
        blobStore.listSegments().
            forEach( segment -> {
                final RepositoryId repositoryId = RepositorySegmentUtils.toRepositoryId( segment );
                if ( isRepositoryToKeep( repositoryId ) )
                {
                    result.inUse();
                }
                else
                {
                    toBeRemoved.add( segment );
                }
                result.processed();
            } );

        toBeRemoved.forEach( segment -> {
            try
            {
                blobStore.deleteSegment( segment );
                result.deleted();
            }
            catch ( Exception e )
            {
                LOG.error( "Failed to delete segment [" + segment + "]", e );
                result.failed();
            }
        } );

        return result.build();
    }

    private boolean isRepositoryToKeep( final RepositoryId repositoryId )
    {
        return repositoryPresenceMap.computeIfAbsent( repositoryId, key -> {
            LOG.info( "Old repository?" + key );

            //If repository is not present, find if there is an old version more recent than the threshold
            final Context systemContext = ContextBuilder.from( ContextAccessor.current() ).
                repositoryId( SystemConstants.SYSTEM_REPO_ID ).
                branch( SystemConstants.BRANCH_SYSTEM ).
                build();
            final Instant since = Instant.now().minusMillis( params.getAgeThreshold() );
            final NodeVersionQuery findRecentVersionsQuery = NodeVersionQuery.create().
                nodeId( NodeId.from( repositoryId.toString() ) ).
                addQueryFilter( RangeFilter.create().
                    fieldName( VersionIndexPath.TIMESTAMP.getPath() ).
                    from( ValueFactory.newDateTime( since ) ).
                    build() ).
                size( 0 ).
                addOrderBy( FieldOrderExpr.create( VersionIndexPath.TIMESTAMP, OrderExpr.Direction.DESC ) ).
                build();
            final NodeVersionQueryResult result = systemContext.callWith( () -> nodeService.findVersions( findRecentVersionsQuery ) );

            LOG.info( "Found " + result.getTotalHits() + " recent versions" );

            return result.getTotalHits() > 0;
        } );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private BlobStore blobStore;

        private RepositoryService repositoryService;

        private NodeService nodeService;

        private String name;

        private VacuumTaskParams params;

        public Builder()
        {
        }

        public Builder blobStore( final BlobStore val )
        {
            blobStore = val;
            return this;
        }

        public Builder repositoryService( final RepositoryService val )
        {
            repositoryService = val;
            return this;
        }

        public Builder nodeService( final NodeService val )
        {
            nodeService = val;
            return this;
        }

        public Builder name( final String val )
        {
            name = val;
            return this;
        }

        public Builder params( final VacuumTaskParams val )
        {
            params = val;
            return this;
        }

        public SegmentCleanerCommand build()
        {
            return new SegmentCleanerCommand( this );
        }
    }
}
