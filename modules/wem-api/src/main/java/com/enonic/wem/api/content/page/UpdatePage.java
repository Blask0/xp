package com.enonic.wem.api.content.page;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;

public class UpdatePage
    extends Command<Content>
{
    private ContentId content;

    private PageEditor editor;

    public UpdatePage content( final ContentId contentId )
    {
        this.content = contentId;
        return this;
    }

    public UpdatePage editor( final PageEditor editor )
    {
        this.editor = editor;
        return this;
    }

    @Override
    public void validate()
    {
    }

    public ContentId getContent()
    {
        return content;
    }

    public PageEditor getEditor()
    {
        return editor;
    }
}
