package com.enonic.wem.admin.rest.resource.content.page;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.content.page.PageDescriptorJson;
import com.enonic.wem.admin.json.content.page.PageDescriptorListJson;
import com.enonic.wem.admin.rest.resource.content.page.part.GetByModulesParams;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageDescriptorService;
import com.enonic.wem.api.content.page.PageDescriptors;
import com.enonic.wem.api.module.ModuleKeys;

@Path("content/page/descriptor")
@Produces(MediaType.APPLICATION_JSON)
public class PageDescriptorResource
{
    private PageDescriptorService pageDescriptorService;

    @GET
    public PageDescriptorJson getByKey( @QueryParam("key") final String pageDescriptorKey )
    {
        final PageDescriptorKey key = PageDescriptorKey.from( pageDescriptorKey );
        final PageDescriptor descriptor = pageDescriptorService.getByKey( key );
        final PageDescriptorJson json = new PageDescriptorJson( descriptor );
        return json;
    }

    @POST
    @Path("list/by_modules")
    @Consumes(MediaType.APPLICATION_JSON)
    public PageDescriptorListJson getByModules( final GetByModulesParams params )
    {
        final ModuleKeys moduleKeys = ModuleKeys.from( params.getModuleKeys() );
        final PageDescriptors pageDescriptors = this.pageDescriptorService.getByModules( moduleKeys );

        return new PageDescriptorListJson( PageDescriptors.from( pageDescriptors ) );
    }

    public void setPageDescriptorService( final PageDescriptorService pageDescriptorService )
    {
        this.pageDescriptorService = pageDescriptorService;
    }
}
