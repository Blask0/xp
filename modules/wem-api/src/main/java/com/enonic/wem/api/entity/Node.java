package com.enonic.wem.api.entity;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.icon.Icon;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.support.ChangeTraceable;
import com.enonic.wem.api.support.illegaledit.IllegalEdit;
import com.enonic.wem.api.support.illegaledit.IllegalEditAware;
import com.enonic.wem.api.support.illegaledit.IllegalEditException;

import static com.enonic.wem.api.support.PossibleChange.newPossibleChange;

public final class Node
    extends Entity
    implements ChangeTraceable, IllegalEditAware<Node>
{
    private final NodeName name;

    private final NodePath parent;

    private final NodePath path;

    private final UserKey modifier;

    private final UserKey creator;

    // TODO: Remove
    private final Icon icon;

    private Node( final BaseBuilder builder )
    {
        super( builder );

        this.creator = builder.creator;

        this.name = builder.name;
        this.parent = builder.parent;

        this.path = this.parent != null && this.name != null ? new NodePath( this.parent, this.name ) : null;

        this.modifier = builder.modifier;
        this.icon = builder.icon;
    }

    public void validateForIndexing()
    {
        Preconditions.checkNotNull( this.id, "Id must be set" );
        Preconditions.checkNotNull( this.entityIndexConfig, "EntityIndexConfig must be set" );
    }

    public void validateForPersistence()
    {
        Preconditions.checkNotNull( this.createdTime, "createdTime must be set" );
        Preconditions.checkNotNull( this.id, "Id must be set" );
        Preconditions.checkNotNull( this.name, "Name must be set" );
        Preconditions.checkNotNull( this.creator, "creator must be set" );
        Preconditions.checkNotNull( this.parent, "parent must be set" );
    }

    public NodeName name()
    {
        return name;
    }

    public NodePath parent()
    {
        return parent;
    }

    public NodePath path()
    {
        return path;
    }

    public UserKey creator()
    {
        return creator;
    }

    public UserKey getCreator()
    {
        return creator;
    }

    public UserKey modifier()
    {
        return modifier;
    }

    public UserKey getModifier()
    {
        return modifier;
    }

    public Icon icon()
    {
        return icon;
    }


    @Override
    public void checkIllegalEdit( final Node to )
        throws IllegalEditException
    {
        // TODO: Unfortunately Java does not like us to also let super class implement checkIllegalEdit(Entity)
        // TODO: Therefor it's here... :(
        IllegalEdit.check( "id", this.id(), to.id(), Node.class );
        IllegalEdit.check( "createdTime", this.getCreatedTime(), to.getCreatedTime(), Node.class );
        IllegalEdit.check( "modifiedTime", this.getModifiedTime(), to.getModifiedTime(), Node.class );

        IllegalEdit.check( "parent", this.parent(), to.parent(), Node.class );
        IllegalEdit.check( "creator", this.creator(), to.creator(), Node.class );
        IllegalEdit.check( "modifier", this.modifier(), to.modifier(), Node.class );
    }

    public static Builder newNode()
    {
        return new Builder();
    }

    public static Builder newNode( final EntityId id )
    {
        return new Builder( id );
    }

    public static Builder newNode( final EntityId id, final NodeName name )
    {
        return new Builder( id, name );
    }

    public static Builder newNode( final Node node )
    {
        return new Builder( node );
    }

    public static EditBuilder editNode( final Node original )
    {
        return new EditBuilder( original );
    }


    public static class BaseBuilder
        extends Entity.BaseBuilder
    {
        NodeName name;

        NodePath parent;

        UserKey modifier;

        UserKey creator;

        Icon icon;

        BaseBuilder()
        {
        }

        BaseBuilder( final EntityId id )
        {
            super( id );
        }

        BaseBuilder( final Node node )
        {
            super( node );

            this.name = node.name;
            this.parent = node.parent;
            this.creator = node.creator;
            this.modifier = node.modifier;
            this.icon = node.icon;
        }

        BaseBuilder( final EntityId id, final NodeName name )
        {
            this.id = id;
            this.name = name;
        }
    }

    public static class EditBuilder
        extends Entity.EditBuilder<EditBuilder>
    {
        private final Node originalNode;

        private NodeName name;

        private Icon icon;

        public EditBuilder( final Node original )
        {
            super( original );
            this.name = original.name;
            this.icon = original.icon;
            this.originalNode = original;
        }

        public EditBuilder name( final NodeName value )
        {
            changes.recordChange( newPossibleChange( "name" ).from( this.originalNode.name ).to( value ).build() );
            this.name = value;
            return this;
        }

        public EditBuilder icon( final Icon value )
        {
            changes.recordChange( newPossibleChange( "icon" ).from( this.originalNode.icon ).to( value ).build() );
            this.icon = value;
            return this;
        }

        public Node build()
        {
            Node.BaseBuilder baseBuilder = new BaseBuilder( this.originalNode );
            baseBuilder.data = this.data;
            baseBuilder.entityIndexConfig = this.entityIndexConfig;

            baseBuilder.name = this.name;
            baseBuilder.icon = this.icon;
            return new Node( baseBuilder );
        }
    }

    public static class Builder
        extends Entity.Builder<Builder>
    {
        private NodeName name;

        private Icon icon;

        private NodePath parent;

        private UserKey modifier;

        private UserKey creator;


        public Builder()
        {
            super();
        }

        public Builder( final EntityId id )
        {
            super( id );
        }

        public Builder( final Node node )
        {
            super( node );

        }

        public Builder( final EntityId id, final NodeName name )
        {
            this.id = id;
            this.name = name;
        }

        public Builder name( final NodeName value )
        {
            this.name = value;
            return this;
        }

        public Builder path( final String value )
        {
            this.parent = new NodePath( value );
            return this;
        }

        public Builder parent( final NodePath value )
        {
            this.parent = value;
            return this;
        }

        public Builder creator( final UserKey value )
        {
            this.creator = value;
            return this;
        }

        public Builder modifier( final UserKey value )
        {
            this.modifier = value;
            return this;
        }

        public Builder icon( final Icon value )
        {
            this.icon = value;
            return this;
        }

        public Node build()
        {
            BaseBuilder baseBuilder = new BaseBuilder();
            baseBuilder.id = this.id;
            baseBuilder.createdTime = this.createdTime;
            baseBuilder.modifiedTime = this.modifiedTime;
            baseBuilder.data = this.data;

            baseBuilder.name = this.name;
            baseBuilder.parent = this.parent;
            baseBuilder.creator = this.creator;
            baseBuilder.modifier = this.modifier;
            baseBuilder.icon = this.icon;
            baseBuilder.entityIndexConfig = this.entityIndexConfig;
            return new Node( baseBuilder );
        }
    }
}
