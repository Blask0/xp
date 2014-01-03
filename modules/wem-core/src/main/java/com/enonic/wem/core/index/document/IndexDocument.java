package com.enonic.wem.core.index.document;


import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexType;

public class IndexDocument
{

    private final EntityId id;

    private final IndexType indexType;

    private final Index index;

    private final ImmutableSet<AbstractIndexDocumentItem> indexDocumentItems;

    private boolean refreshOnStore = false;

    private final String analyzer;

    private final String collection;

    private IndexDocument( final Builder builder )
    {
        this.id = builder.id;
        this.indexType = builder.indexType;
        this.index = builder.index;
        this.indexDocumentItems = ImmutableSet.copyOf( builder.indexDocumentEntries );
        this.analyzer = builder.analyzer;
        this.collection = builder.collection;
    }

    public static Builder newIndexDocument()
    {
        return new Builder();
    }

    public String getId()
    {
        return id.toString();
    }

    public IndexType getIndexType()
    {
        return indexType;
    }

    public Index getIndex()
    {
        return index;
    }

    public Set<AbstractIndexDocumentItem> getIndexDocumentItems()
    {
        return indexDocumentItems;
    }

    public boolean doRefreshOnStore()
    {
        return refreshOnStore;
    }

    public String getAnalyzer()
    {
        return analyzer;
    }

    public String getCollection()
    {
        return collection;
    }

    public static class Builder
    {
        private EntityId id;

        private IndexType indexType;

        private Index index;

        private String analyzer;

        private String collection;

        private Set<AbstractIndexDocumentItem> indexDocumentEntries;

        public Builder()
        {
            indexDocumentEntries = Sets.newHashSet();
        }

        public Builder id( final EntityId id )
        {
            this.id = id;
            return this;
        }

        public Builder indexType( final IndexType indexType )
        {
            this.indexType = indexType;
            return this;
        }

        public Builder index( final Index index )
        {
            this.index = index;
            return this;
        }

        public Builder analyzer( final String analyzer )
        {
            this.analyzer = analyzer;
            return this;
        }

        public Builder addEntry( final AbstractIndexDocumentItem entry )
        {
            this.indexDocumentEntries.add( entry );

            return this;
        }

        public Builder addEntries( final Set<AbstractIndexDocumentItem> entries )
        {
            this.indexDocumentEntries.addAll( entries );
            return this;
        }

        public Builder collection( final String collection )
        {
            this.collection = collection;
            return this;
        }

        public IndexDocument build()
        {
            return new IndexDocument( this );
        }
    }

}

