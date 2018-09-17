package com.enonic.xp.admin.impl.json.schema.content;

import java.time.Instant;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.admin.impl.json.ChangeTraceableJson;
import com.enonic.xp.admin.impl.json.ItemJson;
import com.enonic.xp.admin.impl.rest.resource.schema.content.ContentTypeIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.schema.content.ContentType;

@SuppressWarnings("UnusedDeclaration")
public class ContentTypeSummaryJson
    implements ItemJson, ChangeTraceableJson
{
    private final ContentType contentType;

    private final String iconUrl;

    private final LocaleMessageResolver localeMessageResolver;

    public ContentTypeSummaryJson( final ContentType contentType, final ContentTypeIconUrlResolver iconUrlResolver,
                                   final LocaleMessageResolver localeMessageResolver )
    {
        this.contentType = contentType;
        this.localeMessageResolver = localeMessageResolver;
        this.iconUrl = iconUrlResolver.resolve( contentType );
    }

    public String getName()
    {
        return contentType.getName() != null ? contentType.getName().toString() : null;
    }

    public String getDisplayName()
    {
        if ( StringUtils.isNotBlank( contentType.getDisplayNameI18nKey() ) )
        {
            return localeMessageResolver.localizeMessage( contentType.getDisplayNameI18nKey(), contentType.getDisplayName() );
        }
        else
        {
            return contentType.getDisplayName();
        }
    }

    public String getDescription()
    {
        if ( StringUtils.isNotBlank( contentType.getDescriptionI18nKey() ) )
        {
            return localeMessageResolver.localizeMessage( contentType.getDescriptionI18nKey(), contentType.getDescription() );
        }
        else
        {
            return contentType.getDescription();
        }
    }

    @Override
    public Instant getCreatedTime()
    {
        return contentType.getCreatedTime();
    }

    @Override
    public Instant getModifiedTime()
    {
        return contentType.getModifiedTime();
    }

    public String getIconUrl()
    {
        return iconUrl;
    }

    public String getContentDisplayNameScript()
    {
        return contentType.getContentDisplayNameScript();
    }

    public String getSuperType()
    {
        return contentType.getSuperType() != null ? contentType.getSuperType().toString() : null;
    }

    public boolean isAbstract()
    {
        return contentType.isAbstract();
    }

    public boolean isFinal()
    {
        return contentType.isFinal();
    }

    public boolean isAllowChildContent()
    {
        return contentType.allowChildContent();
    }

    @Override
    public String getCreator()
    {
        return contentType.getCreator() != null ? contentType.getCreator().toString() : null;
    }

    @Override
    public String getModifier()
    {
        return contentType.getModifier() != null ? contentType.getModifier().toString() : null;
    }

    @Override
    public boolean getEditable()
    {
        return false;
    }

    @Override
    public boolean getDeletable()
    {
        return false;
    }
}
