package com.enonic.wem.api.command.content.site;

public class SiteCommands
{
    public CreateSite create()
    {
        return new CreateSite();
    }

    public UpdateSite update()
    {
        return new UpdateSite();
    }

    public DeleteSite delete()
    {
        return new DeleteSite();
    }

    public SiteTemplateCommands template()
    {
        return new SiteTemplateCommands();
    }

    public GetNearestSiteByContentId getNearestSite()
    {
        return new GetNearestSiteByContentId();
    }
}
