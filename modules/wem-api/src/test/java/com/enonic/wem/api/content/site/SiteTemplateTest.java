package com.enonic.wem.api.content.site;

import org.junit.Test;

import com.google.common.collect.Iterators;

import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
import com.enonic.wem.api.content.page.layout.LayoutTemplate;
import com.enonic.wem.api.content.page.layout.LayoutTemplateKey;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.content.page.part.PartTemplate;
import com.enonic.wem.api.content.page.part.PartTemplateKey;
import com.enonic.wem.api.content.page.part.PartTemplateName;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;

import static com.enonic.wem.api.content.page.layout.LayoutRegions.newLayoutRegions;
import static com.enonic.wem.api.content.site.ContentTypeFilter.newContentFilter;
import static com.enonic.wem.api.content.site.Vendor.newVendor;
import static org.junit.Assert.*;

public class SiteTemplateTest
{

    @Test
    public void siteTemplate()
    {
        final ContentTypeFilter contentTypeFilter =
            newContentFilter().defaultDeny().allowContentTypes( ContentTypeNames.from( "com.enonic.intranet", "system.folder" ) ).build();
        SiteTemplate siteTemplate = SiteTemplate.newSiteTemplate().
            key( SiteTemplateKey.from( "Intranet-1.0.0" ) ).
            displayName( "Enonic Intranet" ).
            description( "A social intranet for the Enterprise" ).
            vendor( newVendor().name( "Enonic" ).url( "https://www.enonic.com" ).build() ).
            modules( ModuleKeys.from( "com.enonic.intranet-1.0.0", "com.company.sampleModule-1.1.0", "com.company.theme.someTheme-1.4.1",
                                      "com.enonic.resolvers-1.0.0" ) ).
            contentTypeFilter( contentTypeFilter ).
            rootContentType( ContentTypeName.from( "com.enonic.intranet" ) ).
            build();

        assertEquals( SiteTemplateKey.from( "Intranet-1.0.0" ), siteTemplate.getKey() );
        assertEquals( new SiteTemplateName( "Intranet" ), siteTemplate.getName() );
        assertEquals( new SiteTemplateVersion( "1.0.0" ), siteTemplate.getVersion() );
        assertEquals( "Enonic Intranet", siteTemplate.getDisplayName() );
        assertEquals( "A social intranet for the Enterprise", siteTemplate.getDescription() );
        assertEquals( ModuleKeys.from( "com.enonic.intranet-1.0.0", "com.company.sampleModule-1.1.0", "com.company.theme.someTheme-1.4.1",
                                       "com.enonic.resolvers-1.0.0" ), siteTemplate.getModules() );
        assertEquals( contentTypeFilter, siteTemplate.getContentTypeFilter() );
        assertEquals( ContentTypeName.from( "com.enonic.intranet" ), siteTemplate.getRootContentType() );
        assertEquals( "Enonic", siteTemplate.getVendor().getName() );
        assertEquals( "https://www.enonic.com", siteTemplate.getVendor().getUrl() );
    }

    @Test
    public void siteTemplateWithResources()
    {
        final RootDataSet partTemplateConfig = new RootDataSet();
        partTemplateConfig.addProperty( "width", new Value.Long( 200 ) );

        final PartTemplate partTemplate = PartTemplate.newPartTemplate().
            key( PartTemplateKey.from( "mainmodule|news-part" ) ).
            displayName( "News part template" ).
            config( partTemplateConfig ).
            descriptor( PartDescriptorKey.from( "mainmodule-1.0.0:news-part" ) ).
            build();
        final RootDataSet layoutTemplateConfig = new RootDataSet();
        layoutTemplateConfig.addProperty( "columns", new Value.Long( 3 ) );

        final LayoutTemplate layoutTemplate = LayoutTemplate.newLayoutTemplate().
            key( LayoutTemplateKey.from( "mainmodule|my-layout" ) ).
            displayName( "Layout template" ).
            config( layoutTemplateConfig ).
            descriptor( LayoutDescriptorKey.from( "mainmodule-1.0.0:some-layout" ) ).
            regions( newLayoutRegions().build() ).
            build();

        final RootDataSet pageTemplateConfig = new RootDataSet();
        pageTemplateConfig.addProperty( "pause", new Value.Long( 10000 ) );

        final PageTemplate pageTemplate = PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( "mainmodule.0|main-page" ) ).
            displayName( "Main page template" ).
            config( pageTemplateConfig ).
            canRender( ContentTypeNames.from( "article", "banner" ) ).
            descriptor( PageDescriptorKey.from( "mainmodule-1.0.0:landing-page" ) ).
            build();

        final SiteTemplate siteTemplate = SiteTemplate.newSiteTemplate().
            key( SiteTemplateKey.from( "Intranet-1.0.0" ) ).
            displayName( "Enonic Intranet" ).
            description( "A social intranet for the Enterprise" ).
            addTemplate( pageTemplate ).
            addTemplate( partTemplate ).
            addTemplate( layoutTemplate ).
            build();

        assertEquals( SiteTemplateKey.from( "Intranet-1.0.0" ), siteTemplate.getKey() );
        assertEquals( new SiteTemplateName( "Intranet" ), siteTemplate.getName() );
        assertEquals( new SiteTemplateVersion( "1.0.0" ), siteTemplate.getVersion() );
        assertEquals( "Enonic Intranet", siteTemplate.getDisplayName() );
        assertEquals( "A social intranet for the Enterprise", siteTemplate.getDescription() );
        assertEquals( 3, Iterators.size( siteTemplate.iterator() ) );
        assertEquals( layoutTemplate, siteTemplate.getLayoutTemplates().first() );
        assertEquals( partTemplate, siteTemplate.getPartTemplates().first() );
        assertEquals( partTemplate, siteTemplate.getPartTemplates().getTemplate( new PartTemplateName( "news-part" ) ) );
    }
}
