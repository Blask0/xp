package com.enonic.xp.impl.server.rest.model;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.vacuum.VacuumResult;

public class VacuumResultJson
{
    private final List<VacuumTaskResultJson> taskResults;

    private VacuumResultJson( final List<VacuumTaskResultJson> taskResults )
    {
        this.taskResults = taskResults;
    }

    @SuppressWarnings("unused")
    public List<VacuumTaskResultJson> getTaskResults()
    {
        return taskResults;
    }

    public static VacuumResultJson from( final VacuumResult result )
    {
        return new VacuumResultJson( result.getResults().stream().map( res -> VacuumTaskResultJson.create().
            taskName( res.getTaskName() ).
            deleted( res.getDeleted() ).
            failed( res.getFailed() ).
            inUse( res.getInUse() ).
            processed( res.getProcessed() ).
            build() ).
            collect( Collectors.toList() ) );
    }

    @Override
    public String toString()
    {
        final ObjectMapper mapper = new ObjectMapper();
        try
        {
            return mapper.writeValueAsString( this );
        }
        catch ( JsonProcessingException e )
        {
            throw new RuntimeException( e );
        }
    }
}
