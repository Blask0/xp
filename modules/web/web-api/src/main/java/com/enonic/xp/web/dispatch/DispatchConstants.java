package com.enonic.xp.web.dispatch;

import java.util.List;

import com.google.common.collect.Lists;

public class DispatchConstants
{
    public static final String VIRTUAL_HOST_PREFIX = "@";

    public static final String XP_CONNECTOR = "xp";

    public static final String API_CONNECTOR = "api";

    public static final String STATUS_CONNECTOR = "status";

    public static final List<String> CONNECTORS = Lists.newArrayList( XP_CONNECTOR, API_CONNECTOR, STATUS_CONNECTOR );

    public static final String CONNECTOR_PROPERTY = "connector";
}
