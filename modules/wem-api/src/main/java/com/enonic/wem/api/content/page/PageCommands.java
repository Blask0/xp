package com.enonic.wem.api.content.page;


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
}
