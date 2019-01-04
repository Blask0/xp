package com.enonic.xp.security;

import com.google.common.annotations.Beta;

import com.enonic.xp.security.acl.UserStoreAccessControlList;

import static com.google.common.base.Preconditions.checkNotNull;

@Beta
public final class UpdateUserStoreParams
{

    private final UserStoreKey userStoreKey;

    private final String displayName;

    private final String description;

    private final IdProviderConfig idProviderConfig;

    private final UserStoreEditor editor;

    private final UserStoreAccessControlList userStorePermissions;

    private UpdateUserStoreParams( final Builder builder )
    {
        this.userStoreKey = checkNotNull( builder.userStoreKey, "userStoreKey is required" );
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.idProviderConfig = builder.idProviderConfig;
        this.editor = builder.editor;
        this.userStorePermissions = builder.userStorePermissions;
    }

    public UserStoreKey getKey()
    {
        return userStoreKey;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
    }

    public IdProviderConfig getIdProviderConfig()
    {
        return idProviderConfig;
    }

    public UserStoreEditor getEditor()
    {
        return editor;
    }

    public UserStoreAccessControlList getUserStorePermissions()
    {
        return userStorePermissions;
    }

    public UserStore update( final UserStore source )
    {
        if ( this.editor != null )
        {
            final EditableUserStore editableUserStore = new EditableUserStore( source );
            editor.edit( editableUserStore );
            return editableUserStore.build();
        }

        UserStore.Builder result = UserStore.create( source );
        if ( this.displayName != null )
        {
            result.displayName( this.getDisplayName() );
        }

        if ( this.description != null )
        {
            result.description( this.getDescription() );
        }

        if ( this.idProviderConfig != null )
        {
            result.idProviderConfig( this.getIdProviderConfig() );
        }

        return result.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final UserStore userStore )
    {
        return new Builder( userStore );
    }

    public static class Builder
    {
        private UserStoreKey userStoreKey;

        private String displayName;

        private String description;

        private IdProviderConfig idProviderConfig;

        private UserStoreEditor editor;

        private UserStoreAccessControlList userStorePermissions;

        private Builder()
        {
        }

        private Builder( final UserStore userStore )
        {
            this.userStoreKey = userStore.getKey();
            this.displayName = userStore.getDisplayName();
            this.description = userStore.getDescription();
            this.idProviderConfig = userStore.getIdProviderConfig();
        }

        public Builder key( final UserStoreKey value )
        {
            this.userStoreKey = value;
            return this;
        }

        public Builder displayName( final String value )
        {
            this.displayName = value;
            return this;
        }

        public Builder description( final String value )
        {
            this.description = value;
            return this;
        }

        public Builder idProviderConfig( final IdProviderConfig value )
        {
            this.idProviderConfig = value;
            return this;
        }

        public Builder editor( final UserStoreEditor value )
        {
            this.editor = value;
            return this;
        }

        public Builder permissions( final UserStoreAccessControlList permissions )
        {
            this.userStorePermissions = permissions;
            return this;
        }

        public UpdateUserStoreParams build()
        {
            return new UpdateUserStoreParams( this );
        }
    }
}
