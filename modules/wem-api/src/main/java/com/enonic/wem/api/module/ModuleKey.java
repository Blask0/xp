package com.enonic.wem.api.module;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

// TODO merge this class with ModuleName
public final class ModuleKey
{
    private static final String SYSTEM_MODULE_NAME = "system";

    public final static ModuleKey SYSTEM = new ModuleKey( SYSTEM_MODULE_NAME );

    private final String name;

    private ModuleKey( final String name )
    {
        this.name = name;
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof ModuleKey ) && ( (ModuleKey) o ).name.equals( this.name );
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public String toString()
    {
        return name;
    }

    // TODO remove method
    public ModuleName getName()
    {
        return ModuleName.from( name );
    }

    public static ModuleKey from( final String name )
    {
        return new ModuleKey( name );
    }

    public static ModuleKey from( final Bundle bundle )
    {
        return ModuleKey.from( bundle.getSymbolicName() );
    }

    public static ModuleKey from( final Class<?> clzz )
    {
        return from( FrameworkUtil.getBundle( clzz ) );
    }
}
