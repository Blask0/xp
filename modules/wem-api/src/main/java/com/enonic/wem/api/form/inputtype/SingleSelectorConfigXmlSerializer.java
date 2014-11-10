package com.enonic.wem.api.form.inputtype;

import org.w3c.dom.Element;

import com.enonic.wem.api.xml.DomBuilder;
import com.enonic.wem.api.xml.DomHelper;

import static com.enonic.wem.api.form.inputtype.SingleSelectorConfig.newSingleSelectorConfig;

final class SingleSelectorConfigXmlSerializer
    extends AbstractInputTypeConfigXmlSerializer
{
    public static final SingleSelectorConfigXmlSerializer DEFAULT = new SingleSelectorConfigXmlSerializer();

    @Override
    protected void serializeConfig( final InputTypeConfig config, final DomBuilder builder )
    {
        builder.start( "options" );

        final SingleSelectorConfig singleSelectorConfig = (SingleSelectorConfig) config;
        builder.start( "selector-type" ).text( singleSelectorConfig.getType().toString() ).end();

        for ( final Option option : singleSelectorConfig.getOptions() )
        {
            builder.start( "option" );
            builder.start( "label" ).text( option.getLabel() ).end();
            builder.start( "value" ).text( option.getValue() ).end();
            builder.end();
        }

        builder.end();
    }

    @Override
    public InputTypeConfig parseConfig( final Element elem )
    {
        final SingleSelectorConfig.Builder builder = newSingleSelectorConfig();

        final Element selectorTypeEl = DomHelper.getChildElementByTagName( elem, "selector-type" );
        builder.type( SingleSelectorConfig.SelectorType.valueOf( DomHelper.getTextValue( selectorTypeEl ) ) );

        final Element optionsEl = DomHelper.getChildElementByTagName( elem, "options" );
        for ( final Element optionEl : DomHelper.getChildElementsByTagName( optionsEl, "option" ) )
        {
            final String label = DomHelper.getChildElementValueByTagName( optionEl, "label" );
            final String value = DomHelper.getChildElementValueByTagName( optionEl, "value" );
            builder.addOption( label, value );
        }

        return builder.build();
    }
}
