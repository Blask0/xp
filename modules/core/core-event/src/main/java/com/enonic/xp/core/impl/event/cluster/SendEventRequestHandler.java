package com.enonic.xp.core.impl.event.cluster;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventPublisher;

@Component(immediate = true)
public final class SendEventRequestHandler
    implements MessageListener<SendEventRequest>
{
    private HazelcastInstance hz;

    private EventPublisher eventPublisher;

    @Activate
    public void activate()
    {
        Config cfg = new Config();
        hz = Hazelcast.newHazelcastInstance( cfg );
        ITopic<SendEventRequest> topic = hz.getTopic( ClusterEventSender.ACTION );
        topic.addMessageListener( this );
    }

    @Deactivate
    public void deactivate()
    {
        hz.shutdown();
    }

    @Override
    public void onMessage( final Message<SendEventRequest> message )
    {
        final Event forwardedEvent = Event.create( message.getMessageObject().getEvent() ).
            distributed( false ).
            localOrigin( false ).
            build();
        this.eventPublisher.publish( forwardedEvent );
    }


    @Reference
    public void setEventPublisher( final EventPublisher eventPublisher )
    {
        this.eventPublisher = eventPublisher;
    }
}
