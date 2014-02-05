package com.enonic.wem.admin.rest.resource.content.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.content.query.ContentQuery;
import com.enonic.wem.api.query.parser.QueryParser;
import com.enonic.wem.api.schema.content.ContentTypeNames;

public class ContentQueryJson
{
    private final String expand;

    private final ContentQuery contentQuery;

    @JsonCreator
    ContentQueryJson( @JsonProperty("queryExpr") final String queryExprString, //
                      @JsonProperty("from") final Integer from, //
                      @JsonProperty("size") final Integer size, //
                      @JsonProperty("contentTypeNames") final List<String> contentTypeNameString,
                      @JsonProperty("expand") final String expand,
                      @JsonProperty("aggregationQueries") final List<AggregationQueryJson> aggregationQueries )
    {

        final ContentQuery.Builder builder = ContentQuery.newContentQuery().
            from( from ).
            size( size ).
            queryExpr( QueryParser.parse( queryExprString ) ).
            addContentTypeNames( ContentTypeNames.from( contentTypeNameString ) );

        if ( aggregationQueries != null )
        {
            for ( final AggregationQueryJson aggregationQueryJson : aggregationQueries )
            {
                builder.aggregationQuery( aggregationQueryJson.getAggregationQuery() );
            }
        }

        this.contentQuery = builder.build();
        this.expand = expand != null ? expand : "none";
    }

    @JsonIgnore
    public ContentQuery getContentQuery()
    {
        return contentQuery;
    }

    @JsonIgnore
    public String getExpand()
    {
        return expand;
    }
}
