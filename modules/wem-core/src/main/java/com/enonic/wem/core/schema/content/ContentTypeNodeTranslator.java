package com.enonic.wem.core.schema.content;

import java.util.List;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.entity.CreateNode;
import com.enonic.wem.api.command.entity.UpdateNode;
import com.enonic.wem.api.command.schema.content.CreateContentType;
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
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.FormItems;
import com.enonic.wem.api.schema.SchemaId;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.core.support.SerializerForFormItemToData;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;

public class ContentTypeNodeTranslator
{
    private static final SerializerForFormItemToData SERIALIZER_FOR_FORM_ITEM_TO_DATA = new SerializerForFormItemToData();

    public static final String DISPLAY_NAME_PROPERTY = "displayName";

    public static final String SUPER_TYPE_PROPERTY = "superType";

    public static final String CONTENT_DISPLAY_NAME_SCRIPT_PROPERTY = "contentDisplayNameScript";

    public static final String ALLOW_CHILD_CONTENT_PROPERTY = "allowChildContent";

    public static final String BUILT_IN_PROPERTY = "builtIn";

    public static final String ABSTRACT_PROPERTY = "abstract";

    public static final String FINAL_PROPERTY = "final";

    public static final String FORM_PATH = "form";

    public static final String FORMITEMS_DATA_PATH = "formItems";

    public static final String FORMITEMS_FULL_PATH = "form.formItems";

    public CreateNode toCreateNodeCommand( final CreateContentType command )
    {
        final NodePath parentItemPath = NodePath.newPath( "/content-types" ).build();

        return createNode( command, parentItemPath );
    }

    private CreateNode createNode( final CreateContentType command, final NodePath parentItemPath )
    {
        final RootDataSet rootDataSet = propertiesToRootDataSet( command );
        final EntityIndexConfig entityIndexConfig = ContentTypeEntityIndexConfigFactory.create( rootDataSet );

        return Commands.node().create().
            name( command.getName().toString() ).
            parent( parentItemPath ).
            icon( command.getIcon() ).
            data( rootDataSet ).
            entityIndexConfig( entityIndexConfig );
    }

    public RootDataSet propertiesToRootDataSet( final CreateContentType command )
    {
        final RootDataSet rootDataSet = new RootDataSet();
        addPropertyIfNotNull( rootDataSet, DISPLAY_NAME_PROPERTY, command.getDisplayName() );
        addPropertyIfNotNull( rootDataSet, SUPER_TYPE_PROPERTY, command.getSuperType() );
        addPropertyIfNotNull( rootDataSet, CONTENT_DISPLAY_NAME_SCRIPT_PROPERTY, command.getContentDisplayNameScript() );
        addPropertyIfNotNull( rootDataSet, ALLOW_CHILD_CONTENT_PROPERTY, Boolean.toString( command.getAllowChildContent() ) );
        addPropertyIfNotNull( rootDataSet, BUILT_IN_PROPERTY, Boolean.toString( command.isBuiltIn() ) );
        addPropertyIfNotNull( rootDataSet, ABSTRACT_PROPERTY, Boolean.toString( command.isAbstract() ) );
        addPropertyIfNotNull( rootDataSet, FINAL_PROPERTY, Boolean.toString( command.isFinal() ) );

        final DataSet form = new DataSet( FORM_PATH );
        final DataSet formItems = new DataSet( FORMITEMS_DATA_PATH );
        form.add( formItems );

        for ( Data data : SERIALIZER_FOR_FORM_ITEM_TO_DATA.serializeFormItems( command.getForm().getFormItems() ) )
        {
            formItems.add( data );
        }
        rootDataSet.add( form );

        return rootDataSet;
    }


    UpdateNode toUpdateNodeCommand( final SchemaId id, final NodeEditor editor )
    {
        return Commands.node().update().
            item( EntityId.from( id ) ).
            editor( editor );
    }

    NodeEditor toNodeEditor( final ContentType contentType )
    {
        final RootDataSet rootDataSet = new RootDataSet();
        addPropertyIfNotNull( rootDataSet, DISPLAY_NAME_PROPERTY, contentType.getDisplayName() );
        addPropertyIfNotNull( rootDataSet, SUPER_TYPE_PROPERTY, contentType.getSuperType() );
        addPropertyIfNotNull( rootDataSet, CONTENT_DISPLAY_NAME_SCRIPT_PROPERTY, contentType.getContentDisplayNameScript() );
        addPropertyIfNotNull( rootDataSet, ALLOW_CHILD_CONTENT_PROPERTY, Boolean.toString( contentType.allowChildContent() ) );
        addPropertyIfNotNull( rootDataSet, BUILT_IN_PROPERTY, Boolean.toString( contentType.isBuiltIn() ) );
        addPropertyIfNotNull( rootDataSet, ABSTRACT_PROPERTY, Boolean.toString( contentType.isAbstract() ) );
        addPropertyIfNotNull( rootDataSet, FINAL_PROPERTY, Boolean.toString( contentType.isFinal() ) );

        final DataSet form = new DataSet( FORM_PATH );
        final DataSet formItems = new DataSet( FORMITEMS_DATA_PATH );
        form.add( formItems );
        final List<Data> dataList = SERIALIZER_FOR_FORM_ITEM_TO_DATA.serializeFormItems( contentType.form().getFormItems() );

        for ( final Data data : dataList )
        {
            formItems.add( data );
        }
        rootDataSet.add( form );

        return new NodeEditor()
        {
            @Override
            public Node.EditBuilder edit( final Node toBeEdited )
            {
                return Node.editNode( toBeEdited ).
                    name( NodeName.from( contentType.getName().toString() ) ).
                    icon( contentType.getIcon() ).
                    rootDataSet( rootDataSet );
            }
        };
    }

    private void addPropertyIfNotNull( final RootDataSet rootDataSet, final String propertyName, final Object value )
    {
        if ( value != null )
        {
            rootDataSet.setProperty( propertyName, new Value.String( value.toString() ) );
        }
    }


    ContentTypes fromNodes( final Nodes nodes )
    {
        final ContentTypes.Builder contentTypesBuilder = ContentTypes.newContentTypes();

        for ( final Node node : nodes )
        {
            contentTypesBuilder.add( fromNode( node ) );
        }

        return contentTypesBuilder.build();

    }

    ContentType fromNode( final Node node )
    {
        final DataSet formItemsAsDataSet = node.dataSet( FORMITEMS_FULL_PATH );
        final FormItems formItems = SERIALIZER_FOR_FORM_ITEM_TO_DATA.deserializeFormItems( formItemsAsDataSet );

        final ContentType.Builder builder = newContentType().
            id( new SchemaId( node.id().toString() ) ).
            name( node.name().toString() ).
            form( Form.newForm().addFormItems( formItems ).build() ).
            createdTime( node.getCreatedTime() ).
            creator( node.getCreator() ).
            modifiedTime( node.getModifiedTime() ).
            modifier( node.getModifier() ).
            icon( node.icon() );

        if ( node.property( DISPLAY_NAME_PROPERTY ) != null )
        {
            builder.displayName( node.property( DISPLAY_NAME_PROPERTY ).getString() );
        }

        if ( node.property( ALLOW_CHILD_CONTENT_PROPERTY ) != null )
        {
            builder.allowChildContent( Boolean.valueOf( node.property( ALLOW_CHILD_CONTENT_PROPERTY ).getString() ) );
        }

        if ( node.property( CONTENT_DISPLAY_NAME_SCRIPT_PROPERTY ) != null )
        {
            builder.contentDisplayNameScript( node.property( CONTENT_DISPLAY_NAME_SCRIPT_PROPERTY ).getString() );
        }

        if ( node.property( SUPER_TYPE_PROPERTY ) != null )
        {
            builder.superType( ContentTypeName.from( node.property( SUPER_TYPE_PROPERTY ).getString() ) );
        }

        if ( node.property( BUILT_IN_PROPERTY ) != null )
        {
            builder.builtIn( Boolean.valueOf( node.property( BUILT_IN_PROPERTY ).getString() ) );
        }

        if ( node.property( FINAL_PROPERTY ) != null )
        {

            builder.setFinal( Boolean.valueOf( node.property( FINAL_PROPERTY ).getString() ) );
        }
        if ( node.property( ABSTRACT_PROPERTY ) != null )
        {

            builder.setAbstract( Boolean.valueOf( node.property( ABSTRACT_PROPERTY ).getString() ) );
        }

        return builder.build();
    }
}
