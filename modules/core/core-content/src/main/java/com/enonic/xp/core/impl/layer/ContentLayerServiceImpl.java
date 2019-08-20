package com.enonic.xp.core.impl.layer;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.layer.ContentLayer;
import com.enonic.xp.layer.ContentLayerConstants;
import com.enonic.xp.layer.ContentLayerException;
import com.enonic.xp.layer.ContentLayerName;
import com.enonic.xp.layer.ContentLayerService;
import com.enonic.xp.layer.ContentLayers;
import com.enonic.xp.layer.CreateContentLayerParams;
import com.enonic.xp.layer.GetContentLayerIconResult;
import com.enonic.xp.layer.UpdateContentLayerParams;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repository.CreateBranchParams;
import com.enonic.xp.repository.DeleteBranchParams;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.util.Exceptions;

@Component
public class ContentLayerServiceImpl
    implements ContentLayerService
{
    private NodeService nodeService;

    private RepositoryService repositoryService;

    @Override
    public ContentLayers list()
    {
        return createContext().callWith( this::doList );
    }

    private ContentLayers doList()
    {
        final ValueFilter valueFilter = ValueFilter.create().
            fieldName( NodeIndexPath.NODE_TYPE.getPath() ).
            addValue( ValueFactory.newString( ContentLayerConstants.NODE_TYPE ) ).
            build();

        final NodeQuery nodeQuery = NodeQuery.create().
            addQueryFilter( valueFilter ).
            build();

        final FindNodesByQueryResult result = nodeService.findByQuery( nodeQuery );

        final List<ContentLayer> contentLayers = result.getNodeIds().
            stream().
            map( nodeService::getById ).
            filter( Objects::nonNull ).
            map( this::toContentLayer ).
            collect( Collectors.toList() );

        return ContentLayers.from( contentLayers );
    }

    @Override
    public ContentLayer get( final ContentLayerName name )
    {
        return createContext().callWith( () -> doGet( name ) );
    }

    private ContentLayer doGet( final ContentLayerName name )
    {
        final Node node = nodeService.getByPath( toNodePath( name ) );
        return toContentLayer( node );
    }

    @Override
    public GetContentLayerIconResult getIcon( final ContentLayerName name )
    {
        return createContext().callWith( () -> doGetIcon( name ) );
    }

    private GetContentLayerIconResult doGetIcon( final ContentLayerName name )
    {
        final GetContentLayerIconResult.Builder result = GetContentLayerIconResult.create();
        final Node node = nodeService.getByPath( toNodePath( name ) );
        if ( node != null )
        {
            final Attachment icon = toContentLayer( node ).getIcon();
            if ( icon != null )
            {
                final ByteSource binary = nodeService.getBinary( node.id(), icon.getBinaryReference() );
                result.name( icon.getName() ).
                    mimeType( icon.getMimeType() ).
                    label( icon.getLabel() ).
                    byteSource( binary );
            }
        }
        return result.build();
    }

    @Override
    public ContentLayer create( final CreateContentLayerParams params )
    {
        return createContext().callWith( () -> doCreate( params ) );
    }

    private ContentLayer doCreate( final CreateContentLayerParams params )
    {
        if ( nodeService.nodeExists( toNodePath( params.getName() ) ) )
        {
            throw new ContentLayerException( MessageFormat.format( "Layer [{0}] already exists", params.getName() ) );
        }

        //Creates branches
        final Branch draftBranch = Branch.from( ContentLayerConstants.BRANCH_PREFIX_DRAFT + params.getName() );
        final Branch masterBranch = Branch.from( ContentLayerConstants.BRANCH_PREFIX_MASTER + params.getName() );
        final ContentLayerName parentLayer = params.getParentName();
        final Branch parentDraftBranch = parentLayer.getDraftBranch();

        final Repository contentRepository = repositoryService.get( ContentConstants.CONTENT_REPO_ID );
        if ( contentRepository == null || contentRepository.getChildBranchInfos( parentDraftBranch ) == null )
        {
            throw new ContentLayerException( MessageFormat.format( "Branch [{0}] not found", parentDraftBranch ) );
        }

        final CreateBranchParams createDraftBranchParams = new CreateBranchParams( draftBranch, parentDraftBranch );
        repositoryService.createBranch( createDraftBranchParams );

        final CreateBranchParams createMasterBranchParams = new CreateBranchParams( masterBranch );
        repositoryService.createBranch( createMasterBranchParams );

        //Creates node representation
        final PropertyTree data = toNodeData( params );
        final CreateNodeParams.Builder createNodeParams = CreateNodeParams.create().
            parent( ContentLayerConstants.LAYER_PARENT_PATH ).
            name( params.getName().getValue() ).
            data( data ).
            nodeType( NodeType.from( ContentLayerConstants.NODE_TYPE ) ).
            inheritPermissions( true );
        if ( params.getIcon() != null )
        {
            createNodeParams.attachBinary( params.getIcon().getBinaryReference(), params.getIcon().getByteSource() );
        }
        final Node createdNode = nodeService.create( createNodeParams.build() );

        return toContentLayer( createdNode );
    }

    @Override
    public ContentLayer update( final UpdateContentLayerParams params )
    {
        return createContext().callWith( () -> doUpdate( params ) );
    }

    private ContentLayer doUpdate( final UpdateContentLayerParams params )
    {
        if ( !nodeService.nodeExists( toNodePath( params.getName() ) ) )
        {
            throw new ContentLayerException( MessageFormat.format( "Layer [{0}] does not exist", params.getName() ) );
        }

        //Updates node representation
        final UpdateNodeParams.Builder updateNodeParams = UpdateNodeParams.create().
            path( toNodePath( params.getName() ) ).
            editor( toBeEdited -> {
                final PropertyTree data = toBeEdited.data;
                data.setString( ContentLayerConstants.DISPLAY_NAME_PROPERTY_PATH, params.getDisplayName() );
                data.setString( ContentLayerConstants.DESCRIPTION_PROPERTY_PATH, params.getDescription() );
                data.setString( ContentLayerConstants.LANGUAGE_PROPERTY_PATH,
                                params.getLanguage() == null ? null : params.getLanguage().toLanguageTag() );

                final CreateAttachment icon = params.getIcon();
                setIcon( icon, data );
            } );
        if ( params.getIcon() != null )
        {
            updateNodeParams.attachBinary( params.getIcon().getBinaryReference(), params.getIcon().getByteSource() );
        }
        final Node updatedNode = nodeService.update( updateNodeParams.build() );
        return toContentLayer( updatedNode );
    }

    @Override
    public ContentLayer delete( final ContentLayerName name )
    {
        return createContext().callWith( () -> doDelete( name ) );
    }

    private ContentLayer doDelete( final ContentLayerName name )
    {
        //Performs checks
        if ( ContentLayerName.DEFAULT_LAYER_NAME.equals( name ) )
        {
            throw new ContentLayerException( "Base layer cannot be delete" );
        }
        final NodePath nodePath = toNodePath( name );
        final Node node = nodeService.getByPath( nodePath );
        if ( node == null )
        {
            throw new ContentLayerException( MessageFormat.format( "Layer [{0}] does not exist", name ) );
        }
        final Repository repository = repositoryService.get( ContextAccessor.current().getRepositoryId() );
        if ( repository.getChildBranchInfos( name.getDraftBranch() ).isNotEmpty() )
        {
            throw new ContentLayerException( MessageFormat.format( "Layer [{0}] has a child layer", name ) );
        }

        //Deletes node representation
        nodeService.deleteById( node.id() );

        //Deletes branch and master branches
        final DeleteBranchParams draftParams = DeleteBranchParams.from( name.getDraftBranch() );
        repositoryService.deleteBranch( draftParams );
        final DeleteBranchParams masterParams = DeleteBranchParams.from( name.getMasterBranch() );
        repositoryService.deleteBranch( masterParams );

        return toContentLayer( node );
    }

    private PropertyTree toNodeData( final CreateContentLayerParams params )
    {
        PropertyTree data = new PropertyTree();
        data.setString( ContentLayerConstants.NAME_PROPERTY_PATH, params.getName().getValue() );
        data.setString( ContentLayerConstants.PARENT_NAME_PROPERTY_PATH,
                        params.getParentName() == null ? null : params.getParentName().getValue() );
        data.setString( ContentLayerConstants.DISPLAY_NAME_PROPERTY_PATH, params.getDisplayName() );
        data.setString( ContentLayerConstants.DESCRIPTION_PROPERTY_PATH, params.getDescription() );
        data.setString( ContentLayerConstants.LANGUAGE_PROPERTY_PATH,
                        params.getLanguage() == null ? null : params.getLanguage().toLanguageTag() );

        final CreateAttachment icon = params.getIcon();
        setIcon( icon, data );

        return data;
    }

    private void setIcon( final CreateAttachment icon, final PropertyTree data )
    {
        if ( icon != null )
        {

            final PropertySet iconSet = data.newSet();
            data.setSet( ContentLayerConstants.ICON_PROPERTY_PATH.toString(), iconSet );
            iconSet.setString( ContentLayerConstants.ICON_NAME_PROPERTY_PATH, icon.getName() );
            iconSet.setString( ContentLayerConstants.ICON_LABEL_PROPERTY_PATH, icon.getLabel() );
            iconSet.setBinaryReference( "binary", icon.getBinaryReference() );
            iconSet.setString( ContentLayerConstants.ICON_MIMETYPE_PROPERTY_PATH, icon.getMimeType() );
            try
            {
                iconSet.setLong( ContentLayerConstants.ICON_SIZE_PROPERTY_PATH, icon.getByteSource().size() );
            }
            catch ( IOException e )
            {
                throw Exceptions.unchecked( e );
            }
        }
    }

    private NodePath toNodePath( final ContentLayerName name )
    {
        return NodePath.create( ContentLayerConstants.LAYER_PARENT_PATH, name.getValue() ).build();
    }

    private Context createContext()
    {
        return ContextBuilder.from( ContextAccessor.current() ).
            branch( ContentConstants.BRANCH_MASTER ).
            build();
    }

    private ContentLayer toContentLayer( final Node node )
    {
        if ( node == null )
        {
            return null;
        }
        final String name = node.data().getString( ContentLayerConstants.NAME_PROPERTY_PATH );
        final String parentName = node.data().getString( ContentLayerConstants.PARENT_NAME_PROPERTY_PATH );
        final String displayName = node.data().getString( ContentLayerConstants.DISPLAY_NAME_PROPERTY_PATH );
        final String description = node.data().getString( ContentLayerConstants.DESCRIPTION_PROPERTY_PATH );
        final String language = node.data().getString( ContentLayerConstants.LANGUAGE_PROPERTY_PATH );
        final PropertySet iconSet = node.data().getSet( ContentLayerConstants.ICON_PROPERTY_PATH );
        return ContentLayer.create().
            name( ContentLayerName.from( name ) ).
            parentName( parentName == null ? null : ContentLayerName.from( parentName ) ).
            displayName( displayName ).
            description( description ).
            language( language == null ? null : Locale.forLanguageTag( language ) ).
            icon( toAttachment( iconSet ) ).
            build();
    }

    private Attachment toAttachment( final PropertySet set )
    {
        if ( set != null )
        {
            return Attachment.create().
                name( set.getString( ContentLayerConstants.ICON_NAME_PROPERTY_PATH ) ).
                label( ContentLayerConstants.ICON_LABEL_VALUE ).
                mimeType( set.getString( ContentLayerConstants.ICON_MIMETYPE_PROPERTY_PATH ) ).
                size( set.getLong( ContentLayerConstants.ICON_SIZE_PROPERTY_PATH ) ).
                build();
        }
        return null;
    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    @Reference
    public void setRepositoryService( final RepositoryService repositoryService )
    {
        this.repositoryService = repositoryService;
    }
}
