package com.enonic.xp.core.impl.event.cluster;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.hazelcast.core.Message;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventPublisher;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SendEventRequestHandlerTest
{
    private SendEventRequestHandler sendEventRequestHandler;

    private EventPublisher eventPublisher;


    @BeforeEach
    public void setUp()
    {
        this.eventPublisher = Mockito.mock( EventPublisher.class );

        this.sendEventRequestHandler = new SendEventRequestHandler();
        this.sendEventRequestHandler.setEventPublisher( this.eventPublisher );
    }

    @Test
    public void testMessageReceived()
        throws IOException
    {
        //Creates an event
        Event event = Event.create( "eventType" ).
            timestamp( 123L ).
            distributed( true ).
            value( "key1", "value1" ).
            value( "key2", 1234L ).build();

        //Passes the event received to SendEventRequestHandler
        this.sendEventRequestHandler.onMessage( new Message<>( "", new SendEventRequest( event ), System.currentTimeMillis(), null ) );

        //Checks that the event was correctly published
        ArgumentCaptor<Event> argumentCaptor = ArgumentCaptor.forClass( Event.class );
        Mockito.verify( this.eventPublisher ).publish( argumentCaptor.capture() );
        final Event eventForwarded = argumentCaptor.getValue();
        assertEquals( eventForwarded.getType(), event.getType() );
        assertEquals( eventForwarded.getTimestamp(), event.getTimestamp() );
        assertEquals( eventForwarded.isDistributed(), false );
        assertEquals( eventForwarded.getData(), event.getData() );
    }
}
