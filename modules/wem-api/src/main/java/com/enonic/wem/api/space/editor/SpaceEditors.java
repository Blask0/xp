package com.enonic.wem.api.space.editor;

public abstract class SpaceEditors
{
    public static SpaceEditor composite( final SpaceEditor... editors )
    {
        return new CompositeSpaceEditor( editors );
    }

    public static SpaceEditor setDisplayName( final String displayName )
    {
        return new SetDisplayNameEditor( displayName );
    }

    public static SpaceEditor setIcon( final byte[] icon )
    {
        return new SetSpaceIconEditor( icon );
    }
}
