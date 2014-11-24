package com.enonic.wem.api.data2;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.util.GeoPoint;

public final class ValueTypes
{
    public static final ValueType<PropertySet> PROPERTY_SET = new ValueType.PropertySet();

    public static final ValueType<String> STRING = new ValueType.String();

    public static final ValueType<String> HTML_PART = new ValueType.HtmlPart();

    public static final ValueType<String> XML = new ValueType.Xml();

    public static final ValueType<LocalDate> LOCAL_DATE = new ValueType.LocalDate();

    public static final ValueType<LocalDateTime> LOCAL_DATE_TIME = new ValueType.LocalDateTime();

    public static final ValueType<LocalTime> LOCAL_TIME = new ValueType.LocalTime();

    public static final ValueType<Instant> DATE_TIME = new ValueType.DateTime();

    public static final ValueType<ContentId> CONTENT_ID = new ValueType.ContentId();

    public static final ValueType<Long> LONG = new ValueType.Long();

    public static final ValueType<Double> DOUBLE = new ValueType.Double();

    public static final ValueType<GeoPoint> GEO_POINT = new ValueType.GeoPoint();

    public static final ValueType<Boolean> BOOLEAN = new ValueType.Boolean();

    private static final Map<String, ValueType> typesByName = new HashMap<>();

    static
    {
        register( PROPERTY_SET );
        register( STRING );
        register( HTML_PART );
        register( XML );
        register( LOCAL_DATE );
        register( LOCAL_TIME );
        register( LOCAL_DATE_TIME );
        register( DATE_TIME );
        register( CONTENT_ID );
        register( LONG );
        register( DOUBLE );
        register( GEO_POINT );
        register( BOOLEAN );
    }

    private static void register( ValueType valueType )
    {
        Object previous = typesByName.put( valueType.getName(), valueType );
        Preconditions.checkState( previous == null, "ValueType already registered: " + valueType.getName() );
    }

    public static ValueType getByName( final String name )
    {
        return typesByName.get( name );
    }

}
