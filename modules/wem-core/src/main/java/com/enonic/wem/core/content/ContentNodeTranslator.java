package com.enonic.wem.core.content;

import java.util.List;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.entity.CreateNode;
import com.enonic.wem.api.command.entity.UpdateNode;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIndexConfig;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeEditor;
import com.enonic.wem.api.entity.NodeName;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.FormItems;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.core.support.SerializerForFormItemToData;

public class ContentNodeTranslator
{
    public static final String FORM_PATH = "form";

    public static final String FORMITEMS_DATA_PATH = "formItems";

    public static final String FORMITEMS_FULL_PATH = "form.formItems";

    public static final String CONTENT_DATA_PATH = "contentdata";

    private static final SerializerForFormItemToData SERIALIZER_FOR_FORM_ITEM_TO_DATA = new SerializerForFormItemToData();

    public static final String DRAFT_PATH = "draft";

    public static final String DISPLAY_NAME_PATH = "displayName";

    public static final String CONTENT_TYPE_PATH = "contentType";

    public static final String PARENT_CONTENT_PATH_PATH = "parentContentPath";

    private static final NodePath CONTENTS_ROOT_PATH = NodePath.newPath( "/content" ).build();

    public Node toNode( final Content content )
    {
        final NodePath parentNodePath = NodePath.newPath( CONTENTS_ROOT_PATH ).elements( content.getParentPath().toString() ).build();

        final RootDataSet rootDataSet = propertiesToRootDataSet( content );

        final EntityIndexConfig entityIndexConfig = ContentEntityIndexConfigFactory.create( rootDataSet );

        return Node.newNode().
            id( content.getId() != null ? EntityId.from( content.getId() ) : null ).
            name( NodeName.from( content.getName().toString() ) ).
            parent( parentNodePath ).
            rootDataSet( rootDataSet ).
            entityIndexConfig( entityIndexConfig ).
            build();
    }

    public CreateNode toCreateNode( final Content content )
    {
        final NodePath parentNodePath = NodePath.newPath( CONTENTS_ROOT_PATH ).elements( content.getParentPath().toString() ).build();
        final RootDataSet rootDataSet = propertiesToRootDataSet( content );

        final EntityIndexConfig entityIndexConfig = ContentEntityIndexConfigFactory.create( rootDataSet );

        return new CreateNode().
            data( rootDataSet ).
            entityIndexConfig( entityIndexConfig ).
            parent( parentNodePath ).
            name( content.getName().toString() );
    }


    public RootDataSet propertiesToRootDataSet( final Content content )
    {
        //final Collection<Attachment> attachments = command.getAttachments();

        final RootDataSet rootDataSet = new RootDataSet();

        addContentProperties( content, rootDataSet );
        addContentData( content.getContentData(), rootDataSet );
        addForm( content, rootDataSet );

        return rootDataSet;
    }

    private void addContentProperties( final Content content, final RootDataSet rootDataSet )
    {
        addPropertyIfNotNull( rootDataSet, DRAFT_PATH, content.isDraft() );
        addPropertyIfNotNull( rootDataSet, DISPLAY_NAME_PATH, content.getDisplayName() );
        addPropertyIfNotNull( rootDataSet, CONTENT_TYPE_PATH, content.getType().getContentTypeName() );
        addPropertyIfNotNull( rootDataSet, PARENT_CONTENT_PATH_PATH,
                              content.getPath().getParentPath() != null ? content.getPath().getParentPath().getRelativePath() : null );
    }

    private void addForm( final Content content, final RootDataSet rootDataSet )
    {
        final DataSet form = new DataSet( FORM_PATH );
        final DataSet formItems = new DataSet( FORMITEMS_DATA_PATH );
        form.add( formItems );

        for ( Data formData : SERIALIZER_FOR_FORM_ITEM_TO_DATA.serializeFormItems(
            content.getForm() != null ? content.getForm().getFormItems() : Form.newForm().build().getFormItems() ) )
        {
            formItems.add( formData );
        }
        rootDataSet.add( form );
    }

    private void addContentData( final ContentData contentData, final RootDataSet rootDataSet )
    {
        rootDataSet.add( contentData.toDataSet( CONTENT_DATA_PATH ) );
    }

    public UpdateNode toUpdateNodeCommand( final ContentId id, final NodeEditor editor )
    {
        return Commands.node().update().
            item( EntityId.from( id.toString() ) ).
            editor( editor );
    }

    public NodeEditor toNodeEditor( final Content content )
    {
        final RootDataSet rootDataSet = createRootDataSetForEditor( content );

        return new NodeEditor()
        {
            @Override
            public Node.EditBuilder edit( final Node toBeEdited )
            {
                return Node.editNode( toBeEdited ).
                    name( NodeName.from( content.getName().toString() ) ).
                    rootDataSet( rootDataSet );
            }
        };
    }

    private RootDataSet createRootDataSetForEditor( final Content content )
    {
        final RootDataSet rootDataSet = new RootDataSet();

        addPropertyIfNotNull( rootDataSet, DISPLAY_NAME_PATH, content.getDisplayName() );
        addPropertyIfNotNull( rootDataSet, CONTENT_TYPE_PATH, content.getType().getContentTypeName() );
        addPropertyIfNotNull( rootDataSet, PARENT_CONTENT_PATH_PATH, content.getPath().getParentPath().toString() );
        addFormInRootDataSet( content, rootDataSet );

        return rootDataSet;
    }

    private void addFormInRootDataSet( final Content content, final RootDataSet rootDataSet )
    {
        if ( content.getForm() != null )
        {
            final DataSet form = new DataSet( FORM_PATH );
            final DataSet formItems = new DataSet( FORMITEMS_DATA_PATH );
            form.add( formItems );

            final List<Data> dataList = SERIALIZER_FOR_FORM_ITEM_TO_DATA.serializeFormItems( content.getForm().getFormItems() );

            for ( final Data data : dataList )
            {
                formItems.add( data );
            }
            rootDataSet.add( form );
        }
    }

    private void addPropertyIfNotNull( final RootDataSet rootDataSet, final String propertyName, final Object value )
    {
        if ( value != null )
        {
            rootDataSet.setProperty( propertyName, new Value.String( value.toString() ) );
        }
    }

    public Content fromNode( final Node node )
    {
        final DataSet formItemsAsDataSet = node.dataSet( FORMITEMS_FULL_PATH );
        final FormItems formItems = SERIALIZER_FOR_FORM_ITEM_TO_DATA.deserializeFormItems( formItemsAsDataSet );

        final Content.Builder builder = Content.newContent().
            id( ContentId.from( node.id().toString() ) ).
            parentPath( ContentPath.from( node.path().getParentPath().removeFromBeginning( CONTENTS_ROOT_PATH ).toString() ) ).
            name( node.name().toString() ).
            form( Form.newForm().addFormItems( formItems ).build() ).
            createdTime( node.getCreatedTime() ).
            creator( node.getCreator() ).
            modifiedTime( node.getModifiedTime() ).
            modifier( node.getModifier() );

        if ( node.property( DISPLAY_NAME_PATH ) != null )
        {
            builder.displayName( node.property( DISPLAY_NAME_PATH ).getString() );
        }

        builder.type( ContentTypeName.from( node.property( CONTENT_TYPE_PATH ).getString() ) );

        if ( node.dataSet( CONTENT_DATA_PATH ) != null )
        {
            builder.contentData( new ContentData( node.dataSet( CONTENT_DATA_PATH ).toRootDataSet() ) );
        }

        return builder.build();
    }
}
