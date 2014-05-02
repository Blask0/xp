package com.enonic.wem.api.data.type;

import org.junit.Test;

import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.form.InvalidValueException;

import static org.junit.Assert.*;


public class GeoPointTypeTest
{
    @Test
    public void getLatitude()
    {
        assertEquals( 59.913869, GeoPointType.getLatitude( "59.913869,10.752245" ), 0 );
    }

    @Test
    public void getLongitude()
    {
        assertEquals( 10.752245, GeoPointType.getLongitude( "59.913869,10.752245" ), 0 );
    }

    @Test
    public void given_data_with_value_of_correct_type_but_illegal_value_then_checkValidity_throws_InvalidValueTypeException()
        throws Exception
    {
        // exercise
        ContentData contentData = new ContentData();

        // exercise
        try
        {
            contentData.setProperty( "myGeographicCoordinate", Value.newGeoPoint( "90.0,180.2" ) );
            fail( "Expected Exception" );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            assertTrue( "Expected InvalidValueException, got: " + e.getClass().getSimpleName(), e instanceof InvalidValueException );
            assertEquals( "Invalid value: longitude not within range from -180.0 to 180.0: 90.0,180.2", e.getMessage() );
        }
    }
}
