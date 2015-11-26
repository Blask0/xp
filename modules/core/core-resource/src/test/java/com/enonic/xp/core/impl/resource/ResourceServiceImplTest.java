package com.enonic.xp.core.impl.resource;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;

import com.google.common.collect.Lists;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationNotFoundException;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;
import com.enonic.xp.server.RunMode;
import com.enonic.xp.support.ResourceTestHelper;

import static org.junit.Assert.*;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Matchers.eq;

public class ResourceServiceImplTest
{
    private static final String RESOURCE_FILE_NAME = "resource.txt";

    private static final String RESOURCE_2_FILE_NAME = "resource2.txt";

    private static final String RESOURCE_PATH = "/a/b/" + RESOURCE_FILE_NAME;

    private static final String RESOURCE_2_PATH = "/a/" + RESOURCE_2_FILE_NAME;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private ApplicationKey applicationKey;

    private URL resourceUrl;

    private URL resource2Url;

    private Bundle bundle;

    private ResourceServiceImpl resourceService;

    private Application application;

    @Before
    public void before()
    {
        applicationKey = ApplicationKey.from( "myapplication" );

        ResourceTestHelper resourceTestHelper = new ResourceTestHelper( this );
        resourceUrl = resourceTestHelper.getResource( RESOURCE_FILE_NAME );
        resource2Url = resourceTestHelper.getResource( RESOURCE_2_FILE_NAME );

        bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getResource( RESOURCE_PATH ) ).thenReturn( resourceUrl );
        Mockito.when( bundle.getResource( RESOURCE_2_PATH ) ).thenReturn( resource2Url );
        Mockito.when( bundle.getState() ).thenReturn( Bundle.ACTIVE );

        this.application = Mockito.mock( Application.class );
        Mockito.when( application.getKey() ).thenReturn( applicationKey );
        Mockito.when( application.getBundle() ).thenReturn( bundle );

        final ApplicationService applicationService = Mockito.mock( ApplicationService.class );
        Mockito.when( applicationService.getApplication( applicationKey ) ).thenReturn( application );
        Mockito.when( applicationService.getApplication( not( eq( applicationKey ) ) ) ).thenThrow(
            new ApplicationNotFoundException( ApplicationKey.from( "otherapplication" ) ) );

        resourceService = new ResourceServiceImpl();
        resourceService.setApplicationService( applicationService );
    }

    @Test
    public void get_resource()
    {
        ResourceKey resourceKey;
        Resource resource;

        //Retrieves a resource
        resourceKey = ResourceKey.from( applicationKey, RESOURCE_PATH );
        resource = resourceService.getResource( resourceKey );
        assertNotNull( resource );
        assertTrue( resource.exists() );
        assertEquals( resourceKey, resource.getKey() );
        assertEquals( resourceUrl, resource.getUrl() );

        //Retrieves a resource with an incorrect application key
        ApplicationKey incorrectApplicationKey = ApplicationKey.from( "otherapplication" );
        resourceKey = ResourceKey.from( incorrectApplicationKey, RESOURCE_PATH );
        resource = resourceService.getResource( resourceKey );
        assertNotNull( resource );
        assertFalse( resource.exists() );

        //Retrieves a resource with an incorrect resource path
        resourceKey = ResourceKey.from( applicationKey, "c/resource.txt" );
        resource = resourceService.getResource( resourceKey );
        assertNotNull( resource );
        assertFalse( resource.exists() );
    }

    @Test
    public void find_resource_keys()
    {
        ResourceKeys resourceKeys;

        Mockito.when( bundle.findEntries( "/", RESOURCE_FILE_NAME, true ) ).thenReturn(
            Collections.enumeration( Collections.singleton( resourceUrl ) ) );
        Mockito.when( bundle.findEntries( "/a", "*", true ) ).thenReturn(
            Collections.enumeration( Arrays.asList( resourceUrl, resource2Url ) ) );

        //Finds resources for a specific path
        resourceKeys = resourceService.findResourceKeys( applicationKey, "/", RESOURCE_FILE_NAME, true );
        assertEquals( 1, resourceKeys.getSize() );

        //Finds all text resources in a specific folder
        resourceKeys = resourceService.findResourceKeys( applicationKey, "/a", "*", true );
        assertEquals( 2, resourceKeys.getSize() );

        //Finds all resources in an non existing folder
        resourceKeys = resourceService.findResourceKeys( applicationKey, "/b", "*", true );
        assertEquals( 0, resourceKeys.getSize() );

        //Finds all resources for an incorrect application key
        ApplicationKey incorrectApplicationKey = ApplicationKey.from( "otherapplication" );
        resourceKeys = resourceService.findResourceKeys( incorrectApplicationKey, "/", RESOURCE_FILE_NAME, true );
        assertEquals( 0, resourceKeys.getSize() );
    }

    @Test
    public void find_folders()
    {
        ResourceKeys resourceKeys;

        Mockito.when( bundle.getEntryPaths( "/site/pages" ) ).thenReturn(
            Collections.enumeration( Arrays.asList( "/default/", "/rss/", "/readme.md", "/person_default_page.jpg" ) ) );

        //Finds folders for a specific path
        resourceKeys = resourceService.findFolders( applicationKey, "/site/pages" );
        assertEquals( 2, resourceKeys.getSize() );
    }

    @Test
    public void getResource_devMode()
        throws Exception
    {
        this.resourceService.runMode = RunMode.DEV;

        Mockito.when( this.application.getSourcePaths() ).thenReturn(
            Lists.newArrayList( this.temporaryFolder.getRoot().getAbsolutePath() ) );

        final ResourceKey key = ResourceKey.from( this.applicationKey, RESOURCE_PATH );
        final Resource resource1 = this.resourceService.getResource( key );
        assertNotNull( resource1 );
        assertTrue( resource1.exists() );

        final File folder = this.temporaryFolder.newFolder( "a", "b" );
        final File file = new File( folder, RESOURCE_FILE_NAME );
        Files.write( file.toPath(), "hello".getBytes() );

        final Resource resource2 = this.resourceService.getResource( key );
        assertNotNull( resource2 );
        assertTrue( resource2.exists() );
        assertEquals( file.toURI().toURL(), resource2.getUrl() );
    }
}
