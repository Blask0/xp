package com.enonic.wem.api.content.page;

import java.util.Objects;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.module.ModuleKey;

public final class DescriptorKey
{
    protected static final String SEPARATOR = ":";

    private final ModuleKey moduleKey;

    private final ComponentDescriptorName name;

    private final String refString;

    public DescriptorKey( final ModuleKey moduleKey, final ComponentDescriptorName name )
    {
        this.moduleKey = moduleKey;
        this.name = name;
        this.refString = moduleKey.toString() + SEPARATOR + name.toString();
    }

    public ModuleKey getModuleKey()
    {
        return moduleKey;
    }

    public ComponentDescriptorName getName()
    {
        return name;
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

        final DescriptorKey that = (DescriptorKey) o;
        return Objects.equals( this.refString, that.refString );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.refString );
    }

    public String toString()
    {
        return refString;
    }

    public static DescriptorKey from( final String s )
    {
        final String moduleKey = StringUtils.substringBefore( s, SEPARATOR );
        final String descriptorName = StringUtils.substringAfter( s, SEPARATOR );
        return new DescriptorKey( ModuleKey.from( moduleKey ), new ComponentDescriptorName( descriptorName ) );
    }

    public static DescriptorKey from( final ModuleKey moduleKey, final ComponentDescriptorName descriptorName )
    {
        return new DescriptorKey( moduleKey, descriptorName );
    }

    public static DescriptorKey from( final ModuleKey moduleKey, final String descriptorName )
    {
        return new DescriptorKey( moduleKey, new ComponentDescriptorName( descriptorName ) );
    }
}
