package com.enonic.xp.repo.impl.elasticsearch.xcontent;

import java.util.Collection;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.enonic.xp.repo.impl.index.IndexValueNormalizer;

abstract class AbstractXContentBuilderFactory
{

    static XContentBuilder startBuilder()
        throws Exception
    {
        final XContentBuilder result = XContentFactory.jsonBuilder();
        result.startObject();

        return result;
    }

    static void addField( XContentBuilder result, String name, Object value )
        throws Exception
    {
        if ( value == null )
        {
            return;
        }

        if ( value instanceof String )
        {
            value = IndexValueNormalizer.normalize( (String) value );
            result.field( name, value );
        }

        if ( value instanceof Collection )
        {
            if ( ( (Collection) value ).size() == 1 )
            {
                Object next = ( (Collection) value ).iterator().next();
                if ( next instanceof String )
                {
                    result.field( name, IndexValueNormalizer.normalize( (String) next ) );
                }
                else
                {
                    result.field( name, next );
                }
            }
            else
            {
                result.field( name, (Iterable<?>) value );
            }
        }
    }


    static void endBuilder( final XContentBuilder contentBuilder )
        throws Exception
    {
        contentBuilder.endObject();
    }


}
