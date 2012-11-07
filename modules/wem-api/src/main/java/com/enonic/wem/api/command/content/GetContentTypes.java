package com.enonic.wem.api.command.content;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.type.ContentTypeNames;
import com.enonic.wem.api.content.type.ContentTypes;

public final class GetContentTypes
    extends Command<ContentTypes>
{
    private ContentTypeNames contentTypeNames;

    public ContentTypeNames getNames()
    {
        return this.contentTypeNames;
    }

    public GetContentTypes names( final ContentTypeNames contentTypeNames )
    {
        this.contentTypeNames = contentTypeNames;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof GetContentTypes ) )
        {
            return false;
        }

        final GetContentTypes that = (GetContentTypes) o;
        return Objects.equal( this.contentTypeNames, that.contentTypeNames );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.contentTypeNames );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.contentTypeNames, "Content type cannot be null" );
    }
}
