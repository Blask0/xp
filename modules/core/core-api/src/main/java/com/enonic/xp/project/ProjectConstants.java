package com.enonic.xp.project;

import com.enonic.xp.data.PropertyPath;

public class ProjectConstants
{
    public static final String NODE_TYPE = "project";

    public static final PropertyPath NAME_PROPERTY_PATH = PropertyPath.from( "name" );

    public static final PropertyPath DISPLAY_NAME_PROPERTY_PATH = PropertyPath.from( "displayName" );

    public static final PropertyPath DESCRIPTION_PROPERTY_PATH = PropertyPath.from( "description" );

    public static final PropertyPath ICON_PROPERTY_PATH = PropertyPath.from( "icon" );

    public static final PropertyPath ICON_NAME_PROPERTY_PATH = PropertyPath.from( "name" );

    public static final PropertyPath ICON_MIMETYPE_PROPERTY_PATH = PropertyPath.from( "mimeType" );

    public static final PropertyPath ICON_SIZE_PROPERTY_PATH = PropertyPath.from( "size" );

    public static final String ICON_LABEL_VALUE = "icon";
}
