package com.enonic.wem.api.content.page.part;


import com.enonic.wem.api.content.page.AbstractDescriptorBasedPageComponentDataSerializer;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.data.PropertySet;

public class PartComponentDataSerializer
    extends AbstractDescriptorBasedPageComponentDataSerializer<PartComponent, PartComponent>
{

    public void toData( final PartComponent component, final PropertySet parent )
    {
        final PropertySet asData = parent.addSet( PartComponent.class.getSimpleName() );
        applyPageComponentToData( component, asData );
    }

    public PartComponent fromData( final PropertySet asData )
    {
        PartComponent.Builder component = PartComponent.newPartComponent();
        applyPageComponentFromData( component, asData );
        return component.build();
    }

    @Override
    protected DescriptorKey toDescriptorKey( final String s )
    {
        return PartDescriptorKey.from( s );
    }

}
