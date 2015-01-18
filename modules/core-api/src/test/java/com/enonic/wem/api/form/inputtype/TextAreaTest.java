package com.enonic.wem.api.form.inputtype;


import org.junit.Test;

import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.form.BreaksRequiredContractException;

public class TextAreaTest
{
    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_empty_throws_exception()
    {
        new TextArea().checkBreaksRequiredContract( new PropertyTree().setString( "myText", "" ) );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_blank_throws_exception()
    {
        new TextArea().checkBreaksRequiredContract( new PropertyTree().setString( "myText", " " ) );
    }
}
