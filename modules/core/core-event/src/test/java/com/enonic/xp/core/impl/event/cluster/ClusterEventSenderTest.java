package com.enonic.xp.core.impl.event.cluster;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;

import com.enonic.xp.event.Event;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


class ClusterEventSenderTest
{
    private ClusterEventSender clusterEventSender;

    private HazelcastInstance hz;

    private ITopic<Event> topic;

    @BeforeEach
    void setUp()
    {
        //Mocks the Elasticsearch nodes
        Config cfg = new Config();
        hz = Hazelcast.newHazelcastInstance( cfg );
        topic = hz.getTopic( ClusterEventSender.ACTION );

        clusterEventSender = new ClusterEventSender();
        clusterEventSender.activate();
    }

    @AfterEach
    void tearDown()
    {
        clusterEventSender.deactivate();
        hz.shutdown();
    }

    @Test
    void onEvent()
        throws Exception
    {
        AtomicReference<Message<Event>> received = new AtomicReference<>();
        final Event event = Event.create( "aaa" ).distributed( true ).build();

        topic.addMessageListener( received::set );
        this.clusterEventSender.onEvent( event );
        Thread.sleep( 10000 );
        assertNotNull( received.get() );
    }


    @Test
    void onNonDistributableEvent()
    {
        AtomicReference<Message<Event>> received = new AtomicReference<>();

        final Event event = Event.create( "aaa" ).build();
        this.clusterEventSender.onEvent( event );
        topic.addMessageListener( received::set );
        this.clusterEventSender.onEvent( event );
        assertNull( received.get() );
    }
}
