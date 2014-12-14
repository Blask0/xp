package com.enonic.wem.api.node;

import java.util.Objects;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.io.ByteSource;

import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.index.IndexConfigDocument;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.util.BinaryReference;

public class CreateNodeParams
{
    private final NodePath parent;

    private final String name;

    private final PropertyTree data;

    private final Attachments attachments;

    private final IndexConfigDocument indexConfigDocument;

    private final ChildOrder childOrder;

    private final NodeId nodeId;

    private final AccessControlList permissions;

    private final boolean inheritPermissions;

    private final Long manualOrderValue;

    private final InsertManualStrategy insertManualStrategy;

    private final NodeType nodeType;

    private final BinaryAttachments binaryAttachments;

    private CreateNodeParams( Builder builder )
    {
        this.parent = builder.parent;
        this.name = builder.name;
        this.data = builder.data;
        this.attachments = builder.attachments;
        this.indexConfigDocument = builder.indexConfigDocument;
        this.childOrder = builder.childOrder;
        this.nodeId = builder.nodeId;
        this.permissions = builder.permissions;
        this.inheritPermissions = builder.inheritPermissions;
        this.insertManualStrategy = builder.insertManualStrategy;
        this.manualOrderValue = builder.manualOrderValue;
        this.nodeType = builder.nodeType;
        this.binaryAttachments = new BinaryAttachments( ImmutableSet.copyOf( builder.binaryAttachments ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder from( final Node node )
    {
        return new Builder().
            indexConfigDocument( node.getIndexConfigDocument() ).
            permissions( node.getPermissions() ).
            data( node.data() ).
            attachments( node.attachments() ).
            childOrder( node.getChildOrder() ).
            nodeType( node.getNodeType() ).
            name( node.name().toString() ).

            parent( node.parent() );
    }

    public String getName()
    {
        return name;
    }

    public NodePath getParent()
    {
        return parent;
    }

    public PropertyTree getData()
    {
        return data;
    }

    public Attachments getAttachments()
    {
        return attachments;
    }

    public IndexConfigDocument getIndexConfigDocument()
    {
        return indexConfigDocument;
    }

    public ChildOrder getChildOrder()
    {
        return childOrder;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public boolean isInheritPermissions()
    {
        return inheritPermissions;
    }

    public Long getManualOrderValue()
    {
        return manualOrderValue;
    }

    public InsertManualStrategy getInsertManualStrategy()
    {
        return insertManualStrategy;
    }

    public AccessControlList getPermissions()
    {
        return permissions;
    }

    public boolean inheritPermissions()
    {
        return inheritPermissions;
    }

    public NodeType getNodeType()
    {
        return nodeType;
    }

    public BinaryAttachments getBinaryAttachments()
    {
        return binaryAttachments;
    }

    public static final class Builder
    {
        private NodePath parent;

        private String name;

        private PropertyTree data = new PropertyTree();

        private Attachments attachments = Attachments.empty();

        private IndexConfigDocument indexConfigDocument;

        private ChildOrder childOrder;

        private NodeId nodeId;

        private AccessControlList permissions = AccessControlList.empty();

        private boolean inheritPermissions;

        private InsertManualStrategy insertManualStrategy = InsertManualStrategy.FIRST;

        private Long manualOrderValue;

        private NodeType nodeType;

        private Builder()
        {
            this.inheritPermissions = false;
        }

        private Set<BinaryAttachment> binaryAttachments = Sets.newHashSet();

        public Builder setNodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder parent( final NodePath parent )
        {
            this.parent = parent;
            return this;
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder data( final PropertyTree data )
        {
            this.data = data;
            return this;
        }

        public Builder attachments( final Attachments attachments )
        {
            this.attachments = attachments;
            return this;
        }

        public Builder indexConfigDocument( final IndexConfigDocument indexConfigDocument )
        {
            this.indexConfigDocument = indexConfigDocument;
            return this;
        }

        public Builder childOrder( final ChildOrder childOrder )
        {
            this.childOrder = childOrder;
            return this;
        }

        public Builder permissions( final AccessControlList permissions )
        {
            this.permissions = permissions;
            return this;
        }

        public Builder inheritPermissions( final boolean inheritPermissions )
        {
            this.inheritPermissions = inheritPermissions;
            return this;
        }

        public Builder insertManualStrategy( final InsertManualStrategy insertManualStrategy )
        {
            this.insertManualStrategy = insertManualStrategy;
            return this;
        }

        public Builder manualOrderValue( final Long manualOrderValue )
        {
            this.manualOrderValue = manualOrderValue;
            return this;
        }

        public Builder nodeType( final NodeType nodeType )
        {
            this.nodeType = nodeType;
            return this;
        }

        public Builder attachBinary( final BinaryReference binaryReference, final ByteSource byteSource )
        {
            this.binaryAttachments.add( new BinaryAttachment( binaryReference, byteSource ) );
            return this;
        }

        public CreateNodeParams build()
        {
            return new CreateNodeParams( this );
        }
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

        final CreateNodeParams that = (CreateNodeParams) o;
        return Objects.equals( nodeId, that.nodeId ) &&
            Objects.equals( parent, that.parent ) &&
            Objects.equals( name, that.name ) &&
            Objects.equals( data, that.data ) &&
            Objects.equals( attachments, that.attachments ) &&
            Objects.equals( indexConfigDocument, that.indexConfigDocument ) &&
            Objects.equals( childOrder, that.childOrder ) &&
            Objects.equals( permissions, that.permissions ) &&
            Objects.equals( nodeType, that.nodeType ) &&
            Objects.equals( binaryAttachments, that.binaryAttachments ) &&
            inheritPermissions == that.inheritPermissions;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( parent, name, data, attachments, indexConfigDocument, childOrder, nodeId, permissions, inheritPermissions,
                             nodeType, binaryAttachments );
    }
}
