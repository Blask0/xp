package com.enonic.wem.core.index.elastic.result;

import com.enonic.wem.api.facet.QueryFacet;

@Deprecated
public class OldQueryFacetFactory
{

    protected static QueryFacet create( final String facetName, final org.elasticsearch.search.facet.query.QueryFacet facet )
    {
        QueryFacet queryFacet = new QueryFacet( facet.getCount() );
        queryFacet.setName( facetName );

        return queryFacet;
    }


}
