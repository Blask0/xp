package com.enonic.wem.core.content.type.form;

import java.util.Iterator;

import org.jdom.Element;

import com.enonic.wem.api.content.type.form.FormItem;
import com.enonic.wem.api.content.type.form.FormItems;
import com.enonic.wem.core.content.XmlParsingException;
import com.enonic.wem.core.util.JdomHelper;

public final class FormItemsXmlSerializer
{
    private FormItemXmlSerializer formItemSerializer = new FormItemXmlSerializer( this );

    private final JdomHelper jdomHelper = new JdomHelper();

    public void serialize( final Iterable<FormItem> formItems, final Element parentEl )
    {
        for ( FormItem formItem : formItems )
        {
            parentEl.addContent( formItemSerializer.serialize( formItem ) );
        }
    }

    public Element serialize( final Iterable<FormItem> formItems )
    {
        Element itemsEl = new Element( "items" );
        for ( FormItem formItem : formItems )
        {
            itemsEl.addContent( formItemSerializer.serialize( formItem ) );
        }
        return itemsEl;
    }

    public FormItems parse( final Element parentEl )
    {
        final FormItems formItems = new FormItems();
        final Iterator formItemIt = parentEl.getChildren().iterator();
        while ( formItemIt.hasNext() )
        {
            final Element formItemEl = (Element) formItemIt.next();

            try
            {
                formItems.add( formItemSerializer.parse( formItemEl ) );
            }
            catch ( Exception e )
            {
                throw new XmlParsingException( "Failed to parse FormItem: " + this.jdomHelper.serialize( formItemEl, false ), e );
            }
        }

        return formItems;
    }

    public FormItemXmlSerializer getFormItemXmlSerializer()
    {
        return formItemSerializer;
    }
}
