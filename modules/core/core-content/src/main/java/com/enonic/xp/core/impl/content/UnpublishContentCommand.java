package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentState;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.Nodes;

public class UnpublishContentCommand
    extends AbstractContentCommand
{
    private final UnpublishContentParams params;

    private UnpublishContentCommand( final Builder builder )
    {
        super( builder );

        this.params = builder.params;
    }

    public Contents execute()
    {
        final Context context = ContextAccessor.current();

        final Context unpublishContext = ContextBuilder.from( context ).
            branch( params.getUnpublishBranch() ).
            build();

        return unpublishContext.callWith( () -> unpublish() );
    }

    private Contents unpublish()
    {
        final Contents.Builder contents = Contents.create();

        for ( final ContentId contentId : this.params.getContentIds() )
        {
            recursiveUnpublish( NodeId.from( contentId ), this.params.isIncludeChildren(), contents );
        }

        final Contents result = contents.build();

        removePendingDeleteFromDraft( result );

        return result;
    }

    private void recursiveUnpublish( final NodeId nodeId, boolean includeChildren, final Contents.Builder contentsBuilder )
    {
        if ( includeChildren )
        {
            this.nodeService.findByParent( FindNodesByParentParams.create().parentId( nodeId ).build() ).
                getNodes().forEach( childNode -> recursiveUnpublish( childNode.id(), true, contentsBuilder ) );
        }
        final Node node = this.nodeService.deleteById( nodeId );
        if ( node != null )
        {
            contentsBuilder.add( translator.fromNode( node, false ) );
        }
    }

    private void removePendingDeleteFromDraft( final Contents contents )
    {
        final Branch currentBranch = ContextAccessor.current().getBranch();
        if ( !currentBranch.equals( ContentConstants.BRANCH_DRAFT ) )
        {
            final Context draftContext = ContextBuilder.from( ContextAccessor.current() ).
                branch( ContentConstants.BRANCH_DRAFT ).
                build();
            draftContext.callWith( () -> {
                final Nodes draftNodes = this.nodeService.getByIds( makeNodeIds( contents ) );
                for ( final Node draftNode : draftNodes )
                {
                    if ( draftNode.getNodeState().value().equalsIgnoreCase( ContentState.PENDING_DELETE.toString() ) )
                    {
                        this.nodeService.deleteById( draftNode.id() );
                    }
                }
                return null;
            } );
        }
    }

    private NodeIds makeNodeIds( final Contents contents )
    {
        final NodeIds.Builder nodeIds = NodeIds.create();
        for ( final Content content : contents )
        {
            nodeIds.add( NodeId.from( content.getId().toString() ) );
        }
        return nodeIds.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private UnpublishContentParams params;

        public Builder params( final UnpublishContentParams params )
        {
            this.params = params;
            return this;
        }

        @Override
        void validate()
        {
            Preconditions.checkNotNull( params );
        }

        public UnpublishContentCommand build()
        {
            validate();
            return new UnpublishContentCommand( this );
        }
    }

}

