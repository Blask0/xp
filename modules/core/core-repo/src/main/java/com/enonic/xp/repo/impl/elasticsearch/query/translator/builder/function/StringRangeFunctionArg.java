package com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.function;

import com.enonic.xp.repo.impl.elasticsearch.query.translator.SearchQueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexValueType;

public class StringRangeFunctionArg
    extends AbstractRangeFunctionArg<String>
{
    @Override
    public String getFieldName()
    {
        return new SearchQueryFieldNameResolver().resolve( this.fieldName, IndexValueType.STRING );
    }

}
