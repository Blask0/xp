package com.enonic.xp.repo.impl.search;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repo.impl.StorageName;
import com.enonic.xp.repository.RepositoryId;

public class SearchStorageName
    implements StorageName
{
    public final static String STORAGE_INDEX_PREFIX = "search";

    public final static String DIVIDER = "-";

    private final String name;

    private SearchStorageName( final String name )
    {
        this.name = name;
    }

    public static SearchStorageName from( final RepositoryId repositoryId, final Branch branch )
    {
        return new SearchStorageName(
            STORAGE_INDEX_PREFIX + DIVIDER + repositoryId.toString() + DIVIDER + branch.getValue().toLowerCase() );
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

        final SearchStorageName that = (SearchStorageName) o;

        return !( name != null ? !name.equals( that.name ) : that.name != null );

    }

    @Override
    public int hashCode()
    {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String getName()
    {
        return name;
    }
}
