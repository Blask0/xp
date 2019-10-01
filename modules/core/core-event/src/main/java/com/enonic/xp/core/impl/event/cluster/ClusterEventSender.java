package com.enonic.xp.core.impl.event.cluster;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;

@Component(immediate = true)
public final class ClusterEventSender
    implements EventListener
{
    private HazelcastInstance hz;

    private ITopic<SendEventRequest> topic;

    public static final String ACTION = "xp/event";

    @Activate
    public void activate()
    {
        Config cfg = new Config();
        hz = Hazelcast.newHazelcastInstance( cfg );
        topic = hz.getTopic( ACTION );
    }

    @Deactivate
    public void deactivate()
    {
        hz.shutdown();
    }

    @Override
    public void onEvent( final Event event )
    {
        if ( event != null && event.isDistributed() )
        {
            topic.publish( new SendEventRequest( event ) );
        }
    }
}
