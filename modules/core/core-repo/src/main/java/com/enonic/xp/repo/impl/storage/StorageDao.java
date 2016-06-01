package com.enonic.xp.repo.impl.storage;

import java.util.Collection;

import com.enonic.xp.repo.impl.elasticsearch.document.IndexDocument;

public interface StorageDao
{
    String store( final StoreRequest request );

    void store( final Collection<IndexDocument> indexDocuments );

    boolean delete( final DeleteRequest request );

    GetResult getById( final GetByIdRequest request );

    GetResults getByIds( final GetByIdsRequest requests );

    void copy( final CopyRequest request );

}
