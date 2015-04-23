package com.enonic.xp.form;

import org.junit.Test;

import com.enonic.xp.form.inputtype.InputTypes;

import static org.junit.Assert.*;

public class FormItemTest
{
    @Test
    public void setParent()
    {
        FormItemSet myParent = FormItemSet.newFormItemSet().name( "myParent" ).build();
        Input input = Input.create().name( "myField" ).inputType( InputTypes.TEXT_LINE ).build();
        myParent.add( input );
        assertEquals( "myParent.myField", input.getPath().toString() );
    }
}
