package com.enonic.wem.api.data2;

import java.util.ArrayList;
import java.util.List;

public class PropertyTreeJson
{
    public static PropertyTree fromJson( final List<PropertyArrayJson> list )
    {
        final PropertyTree tree = new PropertyTree();
        for ( PropertyArrayJson propertyArrayJson : list )
        {
            propertyArrayJson.fromJson( tree.getRoot() );
        }
        return tree;
    }

    public static List<PropertyArrayJson> toJson( final PropertyTree propertyTree )
    {
        final List<PropertyArrayJson> list = new ArrayList<>();
        for ( final PropertyArray propertyArray : propertyTree.getRoot().getPropertyArrays() )
        {
            list.add( PropertyArrayJson.toJson( propertyArray ) );
        }
        return list;
    }
}
