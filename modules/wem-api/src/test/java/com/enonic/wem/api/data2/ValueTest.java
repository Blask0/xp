package com.enonic.wem.api.data2;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.Test;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.util.GeoPoint;

import static junit.framework.Assert.assertEquals;

public class ValueTest
{
    @Test
    public void tostring()
    {
        assertEquals( "abc", Value.newString( "abc" ).toString() );
        assertEquals( "<div>abc</div>", Value.newHtmlPart( "<div>abc</div>" ).toString() );
        assertEquals( "<xml></xml>", Value.newXml( "<xml></xml>" ).toString() );
        assertEquals( "false", Value.newBoolean( false ).toString() );
        assertEquals( "abc", Value.newContentId( ContentId.from( "abc" ) ).toString() );
        assertEquals( "1.1,-1.1", Value.newGeoPoint( GeoPoint.from( "1.1,-1.1" ) ).toString() );
        assertEquals( "1.1", Value.newDouble( 1.1 ).toString() );
        assertEquals( "1", Value.newLong( 1L ).toString() );
        assertEquals( "2012-01-01", Value.newLocalDate( LocalDate.of( 2012, 1, 1 ) ).toString() );
        assertEquals( "2012-01-01T12:00:00", Value.newLocalDateTime( LocalDateTime.of( 2012, 1, 1, 12, 0, 0 ) ).toString() );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void tostring_PropertySet()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        PropertySet mySet = tree.addSet( "mySet" );
        mySet.addStrings( "strings", "a", "b", "c" );
        tree.getValue( "mySet" ).toString();
    }
}