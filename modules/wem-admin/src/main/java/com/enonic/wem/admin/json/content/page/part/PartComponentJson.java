package com.enonic.wem.admin.json.content.page.part;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.admin.json.content.page.DescriptorBasedComponentJson;
import com.enonic.wem.api.content.page.ComponentName;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.part.PartComponent;
import com.enonic.wem.api.data.PropertyArrayJson;
import com.enonic.wem.api.data.PropertyTreeJson;

import static com.enonic.wem.api.content.page.part.PartComponent.newPartComponent;

@SuppressWarnings("UnusedDeclaration")
public class PartComponentJson
    extends DescriptorBasedComponentJson<PartComponent>
{
    private final PartComponent part;

    public PartComponentJson( final PartComponent component )
    {
        super( component );
        this.part = component;
    }

    @JsonCreator
    public PartComponentJson( @JsonProperty("name") final String name, @JsonProperty("descriptor") final String descriptor,
                              @JsonProperty("config") final List<PropertyArrayJson> config )
    {
        super( newPartComponent().
            name( ComponentName.from( name ) ).
            descriptor( descriptor != null ? DescriptorKey.from( descriptor ) : null ).
            config( config != null ? PropertyTreeJson.fromJson( config ) : null ).
            build() );

        this.part = getComponent();
    }
}
