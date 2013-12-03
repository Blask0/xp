package com.enonic.wem.core.index.query;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.core.index.IndexFieldNameNormalizer;
import com.enonic.wem.core.index.IndexValueType;
import com.enonic.wem.query.expr.CompareExpr;
import com.enonic.wem.query.expr.FieldExpr;
import com.enonic.wem.query.expr.ValueExpr;
import com.enonic.wem.query.queryfilter.ValueQueryFilter;

public class IndexQueryFieldNameResolver
{
    public static String resolve( final CompareExpr compareExpr )
    {
        final FieldExpr field = compareExpr.getField();

        final String baseFieldName = IndexFieldNameNormalizer.normalize( field.getName() );

        final ValueExpr firstValue = compareExpr.getFirstValue();

        return createValueTypeAwareFieldName( baseFieldName, firstValue.getValue() );
    }

    public static String resolve( final ValueQueryFilter valueQueryFilter )
    {
        final String valueQueryFilterFieldName = valueQueryFilter.getFieldName();

        final String baseFieldName = IndexFieldNameNormalizer.normalize( valueQueryFilterFieldName );

        final ImmutableSet<Value> values = valueQueryFilter.getValues();
        final Value firstValue = values.iterator().next();

        return createValueTypeAwareFieldName( baseFieldName, firstValue );
    }

    public static String resolveStringFieldName( final String queryFieldName )
    {
        return IndexFieldNameNormalizer.normalize( queryFieldName );
    }

    public static String resolveOrderByFieldName( final String orderByFieldName )
    {
        return appendIndexValueType( IndexFieldNameNormalizer.normalize( orderByFieldName ), IndexValueType.ORDERBY );
    }

    public static String resolveGeoPointFieldName( final String orderByFieldName )
    {
        return appendIndexValueType( IndexFieldNameNormalizer.normalize( orderByFieldName ), IndexValueType.GEO_POINT );
    }

    private static String createValueTypeAwareFieldName( final String baseFieldName, final Value value )
    {

        if ( value.isDateType() )
        {
            return appendIndexValueType( baseFieldName, IndexValueType.DATETIME );
        }

        if ( value.isNumericType() )
        {
            return appendIndexValueType( baseFieldName, IndexValueType.NUMBER );
        }

        if ( value.isGeoPoint() )
        {
            return appendIndexValueType( baseFieldName, IndexValueType.GEO_POINT );
        }

        return baseFieldName;
    }

    private static String appendIndexValueType( final String baseFieldName, final IndexValueType indexValueType )
    {
        return baseFieldName + IndexValueType.INDEX_VALUE_TYPE_SEPARATOR + indexValueType.getPostfix();
    }

}

