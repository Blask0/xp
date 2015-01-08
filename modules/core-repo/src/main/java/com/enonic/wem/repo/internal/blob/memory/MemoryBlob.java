package com.enonic.wem.repo.internal.blob.memory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.google.common.io.ByteStreams;

import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.blob.BlobKeyCreator;
import com.enonic.wem.api.util.Exceptions;

public final class MemoryBlob
    implements Blob
{
    private final BlobKey key;

    private final byte[] bytes;

    public MemoryBlob( final BlobKey key, final byte[] bytes )
    {
        this.key = key;
        this.bytes = bytes;
    }

    @Override
    public BlobKey getKey()
    {
        return this.key;
    }

    @Override
    public long getLength()
    {
        return this.bytes.length;
    }

    @Override
    public InputStream getStream()
    {
        return new ByteArrayInputStream( this.bytes );
    }

    public static MemoryBlob create( final byte[] bytes )
    {
        try
        {
            final BlobKey key = BlobKeyCreator.createKey( bytes );
            return new MemoryBlob( key, bytes );
        }
        catch ( final IOException e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    public static MemoryBlob create( final InputStream in )
    {
        try
        {
            return create( ByteStreams.toByteArray( in ) );
        }
        catch ( final IOException e )
        {
            throw Exceptions.unchecked( e );
        }
    }
}
