package com.enonic.wem.event.internal;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.event.Event;
import com.enonic.wem.api.event.EventListener;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class EventPublisherImplTest
{
    private EventPublisherImpl eventPublisher;

    @Before
    public final void setUp()
        throws Exception
    {
        eventPublisher = new EventPublisherImpl();
    }

    @Test
    public void testPublishWithoutListeners()
        throws Exception
    {
        final Event event = new TestEvent();
        eventPublisher.publish( event );
    }

    @Test
    public void testPublishOneListener()
        throws Exception
    {
        final EventListener eventListener1 = mock( EventListener.class );
        eventPublisher.addListener( eventListener1 );

        final Event event = new TestEvent();
        eventPublisher.publish( event );

        eventPublisher.removeListener( eventListener1 );
        eventPublisher.publish( event );

        verify( eventListener1, times( 1 ) ).onEvent( any( TestEvent.class ) );
    }

    @Test
    public void testPublishMultipleListeners()
        throws Exception
    {
        final EventListener eventListener1 = mock( EventListener.class );
        eventPublisher.addListener( eventListener1 );

        final EventListener eventListener2 = mock( EventListener.class );
        eventPublisher.addListener( eventListener2 );

        final EventListener eventListener3 = mock( EventListener.class );
        eventPublisher.addListener( eventListener3 );

        final Event event = new TestEvent();
        eventPublisher.publish( event );

        verify( eventListener1, times( 1 ) ).onEvent( any( TestEvent.class ) );
        verify( eventListener2, times( 1 ) ).onEvent( any( TestEvent.class ) );
        verify( eventListener3, times( 1 ) ).onEvent( any( TestEvent.class ) );
    }

    @Test
    public void testPublishExceptionOnListener()
        throws Exception
    {
        final EventListener eventListener1 = mock( EventListener.class );
        eventPublisher.addListener( eventListener1 );

        final EventListener eventListener2 = mock( EventListener.class );
        eventPublisher.addListener( eventListener2 );

        final EventListener eventListener3 = mock( EventListener.class );
        eventPublisher.addListener( eventListener3 );

        doThrow( new RuntimeException( "Error" ) ).when( eventListener2 ).onEvent( any( Event.class ) );

        final Event event = new TestEvent();
        eventPublisher.publish( event );

        verify( eventListener1, times( 1 ) ).onEvent( any( TestEvent.class ) );
        verify( eventListener2, times( 1 ) ).onEvent( any( TestEvent.class ) );
        verify( eventListener3, times( 1 ) ).onEvent( any( TestEvent.class ) );
    }

    class TestEvent
        implements Event
    {
    }
}