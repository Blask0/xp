package com.enonic.wem.api.content.page;


import com.enonic.wem.api.data.PropertySet;

public abstract class DescriptorBasedComponentDataSerializer<TO_DATA_INPUT extends DescriptorBasedComponent, FROM_DATA_OUTPUT extends DescriptorBasedComponent>
    extends ComponentDataSerializer<TO_DATA_INPUT, FROM_DATA_OUTPUT>
{
    protected void applyComponentToData( final DescriptorBasedComponent component, final PropertySet asData )
    {
        super.applyComponentToData( component, asData );
        asData.ifNotNull().setString( "template", component.getDescriptor() != null ? component.getDescriptor().toString() : null );
        asData.addSet( "config", component.getConfig().getRoot().copy( asData.getTree() ) );
    }

    protected void applyComponentFromData( final DescriptorBasedComponent.Builder component, final PropertySet asData )
    {
        super.applyComponentFromData( component, asData );
        if ( asData.hasProperty( "template" ) )
        {
            component.descriptor( toDescriptorKey( asData.getString( "template" ) ) );
        }

        component.config( asData.getSet( "config" ).toTree() );
    }

    protected abstract DescriptorKey toDescriptorKey( final String s );
}
