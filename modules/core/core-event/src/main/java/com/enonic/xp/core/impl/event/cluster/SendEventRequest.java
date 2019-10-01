package com.enonic.xp.core.impl.event.cluster;

import java.io.Serializable;
import java.util.Map;

import com.enonic.xp.event.Event;

public final class SendEventRequest
    implements Serializable
{
    private Event event;

    public SendEventRequest( final Event event )
    {
        this.event = event;
    }

    public Event getEvent()
    {
        return this.event;
    }


    Object writeReplace()
    {
        return new SerializedForm( this.event );
    }

    static class SerializedForm
        implements Serializable
    {
        private final String type;

        private final long timestamp;

        private final boolean distributed;

        private final Map<String, Object> data;

        SerializedForm( Event event )
        {
            type = event.getType();
            timestamp = event.getTimestamp();
            distributed = event.isDistributed();
            data = event.getData();
        }

        Object readResolve()
        {
            final Event.Builder eventBuilder = Event.create( type ).
                timestamp( timestamp ).
                distributed( distributed );
            for ( Map.Entry<String, Object> dataEntry : data.entrySet() )
            {
                eventBuilder.value( dataEntry.getKey(), dataEntry.getValue() );
            }

            return new SendEventRequest( eventBuilder.build() );
        }

        private static final long serialVersionUID = 0;
    }
}
