package com.enonic.wem.portal.internal.content;

import org.mockito.Mockito;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorService;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.PageTemplateService;
import com.enonic.wem.api.content.page.region.ComponentName;
import com.enonic.wem.api.content.page.region.PartComponent;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.xml.mapper.XmlPageDescriptorMapper;
import com.enonic.wem.api.xml.model.XmlPageDescriptor;
import com.enonic.wem.api.xml.serializer.XmlSerializers;
import com.enonic.wem.portal.internal.base.BaseResourceTest;

public abstract class RenderBaseResourceTest<T extends RenderBaseResourceProvider>
    extends BaseResourceTest
{
    protected ContentService contentService;

    protected PageTemplateService pageTemplateService;

    protected PageDescriptorService pageDescriptorService;

    protected T resourceProvider;

    protected ModuleService moduleService;

    @Override
    protected void configure()
        throws Exception
    {
        this.contentService = Mockito.mock( ContentService.class );
        this.pageTemplateService = Mockito.mock( PageTemplateService.class );
        this.pageDescriptorService = Mockito.mock( PageDescriptorService.class );
        this.moduleService = Mockito.mock( ModuleService.class );

        this.resourceProvider.setContentService( this.contentService );
        this.resourceProvider.setPageTemplateService( this.pageTemplateService );
        this.resourceProvider.setPageDescriptorService( this.pageDescriptorService );
        this.resourceProvider.setModuleService( this.moduleService );

        this.servlet.addComponent( this.resourceProvider );
    }

    protected final void setupContentAndSite()
        throws Exception
    {
        final Content content = createPage( "id", "site/somepath/content", "mymodule:ctype", true );

        Mockito.when( this.contentService.getByPath( ContentPath.from( "site/somepath/content" ).asAbsolute() ) ).
            thenReturn( content );

        Mockito.when( this.contentService.getNearestSite( Mockito.isA( ContentId.class ) ) ).
            thenReturn( createSite( "id", "site", "mymodule:contenttypename" ) );

        Mockito.when( this.contentService.getById( content.getId() ) ).
            thenReturn( content );
    }

    protected final void setupNonPageContent()
        throws Exception
    {
        Mockito.when( this.contentService.getByPath( ContentPath.from( "site/somepath/content" ).asAbsolute() ) ).
            thenReturn( createPage( "id", "site/somepath/content", "mymodule:ctype", false ) );

        Mockito.when( this.contentService.getNearestSite( Mockito.isA( ContentId.class ) ) ).
            thenReturn( createSite( "id", "site", "mymodule:contenttypename" ) );
    }

    protected final void setupTemplates()
        throws Exception
    {
        Mockito.when( this.pageTemplateService.getByKey( Mockito.eq( PageTemplateKey.from( "my-page" ) ) ) ).thenReturn(
            createPageTemplate() );

        Mockito.when( this.pageDescriptorService.getByKey( Mockito.isA( DescriptorKey.class ) ) ).thenReturn( createDescriptor() );

    }

    private Content createPage( final String id, final String path, final String contentTypeName, final boolean withPage )
    {
        PropertyTree rootDataSet = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        rootDataSet.addString( "property1", "value1" );

        final Content.Builder content = Content.newContent().
            id( ContentId.from( id ) ).
            path( ContentPath.from( path ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            displayName( "My Content" ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( ContentTypeName.from( contentTypeName ) );

        if ( withPage )
        {
            PageRegions pageRegions = PageRegions.newPageRegions().
                add( Region.newRegion().name( "main-region" ).
                    add( PartComponent.newPartComponent().name( ComponentName.from( "mypart" ) ).
                        build() ).
                    build() ).
                build();

            Page page = Page.newPage().
                template( PageTemplateKey.from( "my-page" ) ).
                regions( pageRegions ).
                config( rootDataSet ).
                build();
            content.page( page );
        }
        return content.build();
    }

    private Site createSite( final String id, final String path, final String contentTypeName )
    {
        PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.addString( "property1", "value1" );

        Page page = Page.newPage().
            template( PageTemplateKey.from( "my-page" ) ).
            config( rootDataSet ).
            build();

        return Site.newSite().
            id( ContentId.from( id ) ).
            path( ContentPath.from( path ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            displayName( "My Content" ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( ContentTypeName.from( contentTypeName ) ).
            page( page ).
            build();
    }

    private PageTemplate createPageTemplate()
    {
        final PropertyTree pageTemplateConfig = new PropertyTree();
        pageTemplateConfig.addLong( "pause", 10000L );

        PageRegions pageRegions = PageRegions.newPageRegions().
            add( Region.newRegion().name( "main-region" ).
                add( PartComponent.newPartComponent().name( ComponentName.from( "mypart" ) ).
                    build() ).
                build() ).
            build();

        final PageTemplate.Builder builder = PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( "abc" ) ).
            canRender( ContentTypeNames.from( "mymodule:article", "mymodule:banner" ) ).
            regions( pageRegions ).
            config( pageTemplateConfig );

        builder.controller( DescriptorKey.from( "mainmodule:landing-page" ) );

        builder.displayName( "Main page emplate" );
        builder.displayName( "Main page emplate" );
        builder.name( "main-page-template" );
        builder.parentPath( ContentPath.ROOT );

        return builder.build();
    }

    private PageDescriptor createDescriptor()
        throws Exception
    {
        final ModuleKey module = ModuleKey.from( "mainmodule" );
        final ComponentDescriptorName name = new ComponentDescriptorName( "mypage" );
        final DescriptorKey key = DescriptorKey.from( module, name );

        final String xml = "<?xml version=\"1.0\"?>\n" +
            "<page-component>\n" +
            "  <display-name>Landing page</display-name>\n" +
            "  <config/>\n" +
            "</page-component>";
        final PageDescriptor.Builder builder = PageDescriptor.newPageDescriptor();

        final XmlPageDescriptor xmlObject = XmlSerializers.pageDescriptor().parse( xml );
        XmlPageDescriptorMapper.fromXml( xmlObject, builder );

        return builder.
            key( key ).
            displayName( "Landing page" ).
            build();
    }
}
