package com.enonic.wem.core.content.site;

import javax.inject.Inject;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.page.CreatePage;
import com.enonic.wem.api.command.content.site.CreateSite;
import com.enonic.wem.api.command.content.site.CreateSiteTemplate;
import com.enonic.wem.api.command.module.CreateModule;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateName;
import com.enonic.wem.api.content.site.*;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.*;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.core.support.BaseInitializer;
import com.enonic.wem.xml.XmlSerializers;
import com.enonic.wem.xml.content.page.PageDescriptorXml;

import static com.enonic.wem.api.command.Commands.page;
import static com.enonic.wem.api.command.Commands.site;
import static com.enonic.wem.api.content.page.PageDescriptor.newPageDescriptor;
import static com.enonic.wem.api.content.page.PageTemplate.newPageTemplate;
import static com.enonic.wem.api.content.site.Vendor.newVendor;
import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.module.ModuleFileEntry.newModuleDirectory;
import static com.google.common.io.ByteStreams.asByteSource;


public class SitesInitializer
    extends BaseInitializer
{
    private Client client;

    private final static ModuleKey DEMO_MODULE_KEY = ModuleKey.from( "demo-1.0.0" );

    private final static SiteTemplateKey BLUMAN_SITE_TEMPLATE_KEY = SiteTemplateKey.from( "Blueman-1.0.0" );

    private Module demoModule;

    private SiteTemplate siteTemplate;

    private PageTemplate mainPageTemplate;

    protected SitesInitializer()
    {
        super( 13, "sites" );
    }

    @Override
    public void initialize()
        throws Exception
    {
        this.demoModule = createDemoModule();
        mainPageTemplate = createPageTemplate( this.demoModule );
        this.siteTemplate = createSiteTemplate( BLUMAN_SITE_TEMPLATE_KEY, ModuleKeys.from( this.demoModule.getKey() ), mainPageTemplate );

        createSite( "bluman trampoliner", "Bluman Trampoliner" );
        createSite( "bluman intranett", "Bluman Intranett" );
    }


    private void createSite( final String name, final String displayName )
    {
        final ContentId content = createSiteContent( name, displayName );

        final ModuleConfig moduleConfig = ModuleConfig.newModuleConfig().
            module( this.demoModule.getModuleKey() ).
            config( createDemoModuleData( "First", "Second" ) ).build();

        final CreateSite createSite = site().create().
            content( content ).
            template( this.siteTemplate.getKey() ).
            moduleConfigs( ModuleConfigs.from( moduleConfig ) );
        client.execute( createSite );

        final CreatePage createPage = page().create().
            content( content ).
            pageTemplate( this.mainPageTemplate.getName() ).
            config( createPageTemplateConfig( "red" ) );
        client.execute( createPage );
    }

    private PageTemplate createPageTemplate( final Module module )
    {
        final ResourcePath pageTemplateController = ResourcePath.from( "controllers/main-page.js" );
        final ModuleResourceKey descriptorModuleResourceKey = new ModuleResourceKey( module.getModuleKey(), pageTemplateController );

        return newPageTemplate().
            name( new PageTemplateName( "mainpage" ) ).
            displayName( "Main Page" ).
            config( createPageTemplateConfig( "blue" ) ).
            descriptor( descriptorModuleResourceKey ).
            build();
    }

    private RootDataSet createPageTemplateConfig( final String backgroundColor )
    {
        RootDataSet data = new RootDataSet();
        data.setProperty( "background-color", new Value.String( backgroundColor ) );
        return data;
    }

    private SiteTemplate createSiteTemplate( final SiteTemplateKey siteTemplateKey, final ModuleKeys moduleKeys,
                                             final PageTemplate pageTemplate )
    {
        final CreateSiteTemplate createSiteTemplate = site().template().create().
            siteTemplateKey( siteTemplateKey ).
            displayName( "Blueman Site Template" ).
            vendor( newVendor().name( "Enonic AS" ).url( "http://www.enonic.com" ).build() ).
            modules( moduleKeys ).
            description( "Demo site template" ).
            url( "http://enonic.net" ).
            rootContentType( ContentTypeName.page() ).
            addTemplate( pageTemplate );

        try
        {
            client.execute( Commands.site().template().delete( siteTemplateKey ) );
        }
        catch (NoSiteTemplateExistsException e) {

        }
        return client.execute( createSiteTemplate );
    }

    private Module createDemoModule()
    {
        final ModuleFileEntry.Builder controllersDir = newModuleDirectory( "controllers" ).
            addFile( "main-page.js", asByteSource( "some_code();".getBytes() ) );

        final ModuleResourceKey controllerResourceKey =
            new ModuleResourceKey( DEMO_MODULE_KEY, ResourcePath.from( "/controllers/main.page.js" ) );

        final PageDescriptor pageDescriptor = newPageDescriptor().
            name( "landing-page" ).
            displayName( "Landing page" ).
            config( createPageDescriptorForm() ).
            controllerResource( controllerResourceKey ).
            build();

        final String pageDescriptorAsString = serialize( pageDescriptor );

        final ModuleFileEntry.Builder componentPagesDir = newModuleDirectory( "pages" ).
            addFile( "landing-page.xml", asByteSource( pageDescriptorAsString.getBytes() ) );

        final ModuleFileEntry.Builder componentsDir = newModuleDirectory( "components" ).
            addEntry( componentPagesDir );

        final ModuleFileEntry moduleDirectoryEntry = ModuleFileEntry.newModuleDirectory( "" ).
            addEntry( controllersDir ).
            addEntry( componentsDir ).
            build();

        final CreateModule createModule = Commands.module().create().
            name( DEMO_MODULE_KEY.getName().toString() ).
            version( DEMO_MODULE_KEY.getVersion() ).
            displayName( "Demo module" ).
            info( "For demo purposes only." ).
            url( "http://enonic.net" ).
            vendorName( "Enonic AS" ).
            vendorUrl( "http://www.enonic.com" ).
            minSystemVersion( ModuleVersion.from( 5, 0, 0 ) ).
            maxSystemVersion( ModuleVersion.from( 6, 0, 0 ) ).
            moduleDependencies( ModuleKeys.empty() ).
            contentTypeDependencies( ContentTypeNames.empty() ).
            config( createDemoModuleForm() ).
            moduleDirectoryEntry( moduleDirectoryEntry );

        try
        {
            client.execute( Commands.module().delete().module( DEMO_MODULE_KEY ) );
        }
        catch (ModuleNotFoundException e) {

        }
        return client.execute( createModule );
    }

    private Form createPageDescriptorForm()
    {

        return Form.newForm().
            addFormItem( newInput().name( "background-color" ).label( "Background color" ).inputType( InputTypes.TEXT_LINE ).build() ).
            addFormItem( newInput().name( "main" ).label( "Main region" ).maximumOccurrences( 1 ).inputType( InputTypes.REGION ).build() ).
            addFormItem(
                newInput().name( "header" ).label( "Header region" ).maximumOccurrences( 1 ).inputType( InputTypes.REGION ).build() ).
            addFormItem(
                newInput().name( "footer" ).label( "Footer region" ).maximumOccurrences( 1 ).inputType( InputTypes.REGION ).build() ).
            build();
    }

    private RootDataSet createDemoModuleData( final String a, final String b )
    {
        RootDataSet data = new RootDataSet();
        data.setProperty( "my-config-a", new Value.String( a ) );
        data.setProperty( "my-config-b", new Value.String( b ) );
        return data;
    }

    private Form createDemoModuleForm()
    {
        return Form.newForm().
            addFormItem( newInput().name( "my-config-a" ).inputType( InputTypes.TEXT_LINE ).build() ).
            addFormItem( newInput().name( "my-config-b" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();
    }

    private ContentId createSiteContent( final String name, final String displayName )
    {
        final CreateContent createContent = Commands.content().create().
            name( name ).
            parent( ContentPath.ROOT ).
            displayName( displayName ).
            contentType( ContentTypeName.page() ).
            form( Form.newForm().build() ).
            contentData( new ContentData() );
        return client.execute( createContent ).getContentId();
    }

    private String serialize( PageDescriptor pageDescriptor )
    {
        final PageDescriptorXml pageDescriptorXml = new PageDescriptorXml();
        pageDescriptorXml.from( pageDescriptor );
        return XmlSerializers.pageDescriptor().serialize( pageDescriptorXml );
    }

    @Inject
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
