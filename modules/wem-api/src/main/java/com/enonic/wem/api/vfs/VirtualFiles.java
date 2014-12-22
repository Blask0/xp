package com.enonic.wem.api.vfs;

import java.io.File;
import java.nio.file.Path;

import org.osgi.framework.Bundle;

public final class VirtualFiles
{
    public static VirtualFile from( final Path path )
    {
        return new LocalFile( path );
    }

    public static VirtualFile from( final File file )
    {
        return from( file.toPath() );
    }

    public static VirtualFile from( final Bundle bundle, final String path )
    {
        return new BundleFile( bundle, path );
    }

    public static VirtualFile from( final ClassLoader classLoader )
    {
        return null;
    }

    public static VirtualFile from( final Class clazz )
    {
        return from( clazz.getClassLoader() );
    }
}
