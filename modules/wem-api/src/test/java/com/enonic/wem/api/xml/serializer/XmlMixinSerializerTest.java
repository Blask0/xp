package com.enonic.wem.api.xml.serializer;

import org.junit.Test;

import com.enonic.wem.api.form.FieldSet;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.Layout;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.xml.mapper.XmlMixinMapper;
import com.enonic.wem.api.xml.model.XmlMixin;

import static com.enonic.wem.api.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;
import static junit.framework.Assert.assertEquals;

public class XmlMixinSerializerTest
    extends BaseXmlSerializer2Test
{
    @Test
    public void test_to_xml()
        throws Exception
    {
        final FormItemSet set = newFormItemSet().name( "mySet" ).build();
        final Layout layout = FieldSet.newFieldSet().label( "My field set" ).name( "myFieldSet" ).addFormItem(
            newInput().name( "myTextLine" ).inputType( InputTypes.TEXT_LINE ).build() ).build();
        set.add( layout );

        final Mixin.Builder builder = newMixin().name( "mymodule-1.0.0:mixin" );
        builder.displayName( "display name" ).description( "description" );

        builder.addFormItem( set );

        final Mixin mixin = builder.build();

        final XmlMixin xml = XmlMixinMapper.toXml( mixin );
        final String result = XmlSerializers2.mixin().serialize( xml );

        assertXml( "mixin.xml", result );
    }

    @Test
    public void test_from_xml()
        throws Exception
    {
        final String xml = readFromFile( "mixin.xml" );
        final Mixin.Builder builder = newMixin();

        final XmlMixin xmlObject = XmlSerializers2.mixin().parse( xml );
        XmlMixinMapper.fromXml( xmlObject, builder );

        builder.name( "mymodule-1.0.0:mymixin" );
        final Mixin mixin = builder.build();
        assertEquals( "mymodule-1.0.0:mymixin", mixin.getName().toString() );
        assertEquals( "display name", mixin.getDisplayName() );
        assertEquals( "description", mixin.getDescription() );

        assertEquals( 1, mixin.getFormItems().size() );
    }
}
