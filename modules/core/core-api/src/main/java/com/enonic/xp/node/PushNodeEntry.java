package com.enonic.xp.node;

public class PushNodeEntry
{
    private NodeBranchEntry nodeBranchEntry;

    private NodeVersionId nodeVersionId;

    private NodePath previousPath;

    private PushNodeEntry( final Builder builder )
    {
        nodeVersionId = builder.nodeVersionId;
        nodeBranchEntry = builder.nodeBranchEntry;
        previousPath = builder.previousPath;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeBranchEntry getNodeBranchEntry()
    {
        return nodeBranchEntry;
    }

    public NodeVersionId getNodeVersionId()
    {
        return nodeVersionId;
    }

    public NodePath getPreviousPath()
    {
        return previousPath;
    }

    @Override
    public int hashCode()
    {
        int result = nodeBranchEntry != null ? nodeBranchEntry.hashCode() : 0;
        result = 31 * result + ( nodeVersionId != null ? nodeVersionId.hashCode() : 0 );
        result = 31 * result + ( previousPath != null ? previousPath.hashCode() : 0 );
        return result;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final PushNodeEntry that = (PushNodeEntry) o;

        if ( nodeBranchEntry != null ? !nodeBranchEntry.equals( that.nodeBranchEntry ) : that.nodeBranchEntry != null )
        {
            return false;
        }
        if ( nodeVersionId != null ? !nodeVersionId.equals( that.nodeVersionId ) : that.nodeVersionId != null )
        {
            return false;
        }
        if ( previousPath != null ? !previousPath.equals( that.previousPath ) : that.previousPath != null )
        {
            return false;
        }
        return true;
    }

    public static final class Builder
    {

        private NodeVersionId nodeVersionId;

        private NodeBranchEntry nodeBranchEntry;

        private NodePath previousPath;

        private Builder()
        {
        }

        public Builder nodeVersionId( final NodeVersionId val )
        {
            nodeVersionId = val;
            return this;
        }

        public Builder nodeBranchEntry( final NodeBranchEntry val )
        {
            nodeBranchEntry = val;
            return this;
        }

        public Builder previousPath( final NodePath val )
        {
            previousPath = val;
            return this;
        }

        public PushNodeEntry build()
        {
            return new PushNodeEntry( this );
        }
    }
}
