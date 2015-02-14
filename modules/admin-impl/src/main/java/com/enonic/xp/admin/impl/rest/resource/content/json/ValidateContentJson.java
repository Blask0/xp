package com.enonic.xp.admin.impl.rest.resource.content.json;


import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.schema.content.validator.DataValidationError;
import com.enonic.xp.schema.content.validator.DataValidationErrors;

public class ValidateContentJson
{
    private boolean hasErrors;

    private List<ErrorJson> errors;

    public ValidateContentJson( final DataValidationErrors validationErrors )
    {
        ImmutableList.Builder<ErrorJson> builder = new ImmutableList.Builder<>();
        if ( validationErrors != null )
        {
            this.hasErrors = validationErrors.hasErrors();
            for ( DataValidationError validationError : validationErrors )
            {
                builder.add( new ErrorJson( validationError ) );
            }
        }
        this.errors = builder.build();
    }

    public boolean isHasErrors()
    {
        return hasErrors;
    }

    public List<ErrorJson> getErrors()
    {
        return errors;
    }

    public class ErrorJson
    {

        private String path;

        private String message;

        public ErrorJson( final DataValidationError error )
        {
            this.path = error.getPath().toString();
            this.message = error.getErrorMessage();
        }

        public String getPath()
        {
            return path;
        }

        public String getMessage()
        {
            return message;
        }
    }

}
