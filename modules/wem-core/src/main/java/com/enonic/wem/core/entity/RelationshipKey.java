package com.enonic.wem.core.entity;


import java.util.Objects;

import com.enonic.wem.api.data.DataPath;

/**
 * The natural key of a Relationship.
 */
public final class RelationshipKey
{
    private final RelationshipTypeName type;

    private final EntityId fromItem;

    private final EntityId toItem;

    /**
     * Path to the Data in the fromItem that is managing this Relationship.
     */
    private final DataPath managingData;

    private RelationshipKey( final RelationshipTypeName type, final EntityId fromContent, final EntityId toItem,
                             final DataPath managingData )
    {
        this.type = type;
        this.fromItem = fromContent;
        this.toItem = toItem;
        this.managingData = managingData;
    }

    private RelationshipKey( final Builder builder )
    {
        this.type = builder.type;
        this.fromItem = builder.fromItem;
        this.toItem = builder.toItem;
        this.managingData = builder.managingData;
    }

    public RelationshipTypeName getType()
    {
        return type;
    }

    public EntityId getFromItem()
    {
        return fromItem;
    }

    public boolean isManaged()
    {
        return managingData != null;
    }

    public DataPath getManagingData()
    {
        return managingData;
    }

    public EntityId getToItem()
    {
        return toItem;
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

        final RelationshipKey that = (RelationshipKey) o;

        return Objects.equals( type, that.type ) &&
            Objects.equals( fromItem, that.fromItem ) &&
            Objects.equals( toItem, that.toItem ) &&
            Objects.equals( managingData, that.managingData );
    }

    @Override
    public String toString()
    {
        return com.google.common.base.Objects.toStringHelper( this ).
            add( "fromItem", fromItem ).
            add( "toItem", toItem ).
            add( "type", type ).
            add( "managingData", managingData ).
            toString();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( type, fromItem, toItem, managingData );
    }

    public static RelationshipKey from( final RelationshipTypeName type, final EntityId fromItem, final EntityId toItem )
    {
        return new RelationshipKey( type, fromItem, toItem, null );
    }

    public static RelationshipKey from( final RelationshipTypeName type, final EntityId fromItem, final DataPath managingData,
                                        final EntityId toItem )
    {
        return new RelationshipKey( type, fromItem, toItem, managingData );
    }

    public static Builder newRelationshipKey()
    {
        return new Builder();
    }

    public static class Builder
    {
        private RelationshipTypeName type;

        private EntityId fromItem;

        private EntityId toItem;

        private DataPath managingData;

        public Builder type( RelationshipTypeName relationshipType )
        {
            this.type = relationshipType;
            return this;
        }

        public Builder fromItem( EntityId contentId )
        {
            this.fromItem = contentId;
            return this;
        }

        public Builder toItem( EntityId contentId )
        {
            this.toItem = contentId;
            return this;
        }

        public Builder managingData( DataPath dataPath )
        {
            this.managingData = dataPath;
            return this;
        }

        public RelationshipKey build()
        {
            return new RelationshipKey( this );
        }
    }
}
