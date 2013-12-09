package com.enonic.wem.api.command.content.page;


public final class PageCommands
{
    public CreatePage create()
    {
        return new CreatePage();
    }

    public UpdatePage update()
    {
        return new UpdatePage();
    }

    public DeletePage delete()
    {
        return new DeletePage();
    }

    public TemplateCommands template()
    {
        return new TemplateCommands();
    }

    public DescriptorCommands descriptor()
    {
        return new DescriptorCommands();
    }
}
