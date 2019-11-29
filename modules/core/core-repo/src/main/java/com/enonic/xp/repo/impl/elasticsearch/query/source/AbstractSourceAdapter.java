package com.enonic.xp.repo.impl.elasticsearch.query.source;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;

abstract class AbstractSourceAdapter
{
    private final static String SEARCH_INDEX_PREFIX = "search";

    private final static String STORAGE_INDEX_PREFIX = "storage";

    private final static String DIVIDER = "-";


    static String createSearchIndexName( final RepositoryId repositoryId, final Branch branch )
    {
        return SEARCH_INDEX_PREFIX + DIVIDER + repositoryId.toString() + DIVIDER + branch.getValue().toLowerCase();
    }

    static String createStorageIndexName( final RepositoryId repositoryId )
    {
        return STORAGE_INDEX_PREFIX + DIVIDER + repositoryId.toString();
    }

    static String createSearchTypeName( final Branch branch )
    {
        return branch.getValue();
    }

}
