package com.enonic.xp.security;

import java.time.Instant;

import com.google.common.annotations.Beta;

@Beta
public final class EditableGroup
{
    public final Group source;

    public PrincipalKey key;

    public String displayName;

    public Instant modifiedTime;

    public EditableGroup( final Group source )
    {
        this.source = source;
        this.displayName = source.getDisplayName();
        this.key = source.getKey();
        this.modifiedTime = source.getModifiedTime();
    }

    public Group build()
    {
        return Group.create( this.source ).
            displayName( displayName ).
            key( key ).
            modifiedTime( modifiedTime ).
            build();
    }
}
