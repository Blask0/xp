package com.enonic.xp.portal.impl.services;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.page.PageDescriptorService;
import com.enonic.wem.api.content.page.PageTemplateService;
import com.enonic.wem.api.image.ImageFilterBuilder;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.portal.internal.controller.ControllerScriptFactory;
import com.enonic.wem.portal.internal.rendering.RendererFactory;

@Component
public final class PortalServicesImpl
    implements PortalServices
{
    private ModuleService moduleService;

    private ControllerScriptFactory controllerScriptFactory;

    private ContentService contentService;

    private ImageFilterBuilder imageFilterBuilder;

    private RendererFactory rendererFactory;

    private PageTemplateService pageTemplateService;

    private PageDescriptorService pageDescriptorService;

    @Override
    public ModuleService getModuleService()
    {
        return this.moduleService;
    }

    @Override
    public ControllerScriptFactory getControllerScriptFactory()
    {
        return this.controllerScriptFactory;
    }

    @Override
    public ContentService getContentService()
    {
        return this.contentService;
    }

    @Override
    public ImageFilterBuilder getImageFilterBuilder()
    {
        return this.imageFilterBuilder;
    }

    @Override
    public RendererFactory getRendererFactory()
    {
        return this.rendererFactory;
    }

    @Override
    public PageTemplateService getPageTemplateService()
    {
        return this.pageTemplateService;
    }

    @Override
    public PageDescriptorService getPageDescriptorService()
    {
        return this.pageDescriptorService;
    }

    @Reference
    public void setModuleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
    }

    @Reference
    public void setControllerScriptFactory( final ControllerScriptFactory controllerScriptFactory )
    {
        this.controllerScriptFactory = controllerScriptFactory;
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Reference
    public void setImageFilterBuilder( final ImageFilterBuilder imageFilterBuilder )
    {
        this.imageFilterBuilder = imageFilterBuilder;
    }

    @Reference
    public void setRendererFactory( final RendererFactory rendererFactory )
    {
        this.rendererFactory = rendererFactory;
    }

    @Reference
    public void setPageTemplateService( final PageTemplateService pageTemplateService )
    {
        this.pageTemplateService = pageTemplateService;
    }

    @Reference
    public void setPageDescriptorService( final PageDescriptorService pageDescriptorService )
    {
        this.pageDescriptorService = pageDescriptorService;
    }
}
