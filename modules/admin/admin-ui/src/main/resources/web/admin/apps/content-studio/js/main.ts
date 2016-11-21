import "./api.ts";
import {Router} from "./app/Router";
import {ContentAppPanel} from "./app/ContentAppPanel";
import {ContentDeletePromptEvent} from "./app/browse/ContentDeletePromptEvent";
import {ContentPublishPromptEvent} from "./app/browse/ContentPublishPromptEvent";
import {ContentUnpublishPromptEvent} from "./app/browse/ContentUnpublishPromptEvent";
import {ContentDeleteDialog} from "./app/remove/ContentDeleteDialog";
import {ContentPublishDialog} from "./app/publish/ContentPublishDialog";
import {ContentUnpublishDialog} from "./app/publish/ContentUnpublishDialog";
import {NewContentDialog} from "./app/create/NewContentDialog";
import {ShowNewContentDialogEvent} from "./app/browse/ShowNewContentDialogEvent";
import {SortContentDialog} from "./app/browse/SortContentDialog";
import {MoveContentDialog} from "./app/browse/MoveContentDialog";
import {EditPermissionsDialog} from "./app/wizard/EditPermissionsDialog";
import {ContentWizardPanelParams} from "./app/wizard/ContentWizardPanelParams";
import {ContentWizardPanel} from "./app/wizard/ContentWizardPanel";
import {ContentEventsListener} from "./app/ContentEventsListener";
import UriHelper = api.util.UriHelper;
import ContentTypeName = api.schema.content.ContentTypeName;
import ContentId = api.content.ContentId;
import AppBarTabId = api.app.bar.AppBarTabId;
import ContentNamedEvent = api.content.event.ContentNamedEvent;
import PropertyChangedEvent = api.PropertyChangedEvent;

declare var CONFIG;

/*
 module components {
 export var detailPanel: app.browse.ContentBrowseItemPanel;
 }
 */

function getApplication(): api.app.Application {
    var application = new api.app.Application('content-studio', 'Content Studio', 'CM', 'content-studio');
    application.setPath(api.rest.Path.fromString(Router.getPath()));
    application.setWindow(window);
    this.serverEventsListener = new api.app.ServerEventsListener([application]);
    this.clientEventsListener = new ContentEventsListener([application]);

    var messageId;
    this.lostConnectionDetector = new api.system.LostConnectionDetector();
    this.lostConnectionDetector.setAuthenticated(true);
    this.lostConnectionDetector.onConnectionLost(() => {
        api.notify.NotifyManager.get().hide(messageId);
        messageId = api.notify.showError("Lost connection to server - Please wait until connection is restored", false);
    });
    this.lostConnectionDetector.onSessionExpired(() => {
        api.notify.NotifyManager.get().hide(messageId);
        window.location.href = api.util.UriHelper.getToolUri("");
    });
    this.lostConnectionDetector.onConnectionRestored(() => {
        api.notify.NotifyManager.get().hide(messageId);
    });

    return application;
}

function initToolTip() {
    var ID = api.StyleHelper.getCls("tooltip", api.StyleHelper.COMMON_PREFIX),
        CLS_ON = "tooltip_ON", FOLLOW = true,
        DATA = "_tooltip", OFFSET_X = 0, OFFSET_Y = 20,
        pageX = 0, pageY = 0,
        showAt = function (e) {
            var ntop = pageY + OFFSET_Y, nleft = pageX + OFFSET_X;
            var tooltipText = api.util.StringHelper.escapeHtml(wemjq(e.target).data(DATA));
            if (!tooltipText) { //if no text then probably hovering over children of original element that has title attr
                return;
            }

            var tooltipWidth = tooltipText.length * 7.5;
            var windowWidth = wemjq(window).width();
            if (nleft + tooltipWidth >= windowWidth) {
                nleft = windowWidth - tooltipWidth;
            }
            wemjq("#" + ID).html(tooltipText).css({
                position: "absolute", top: ntop, left: nleft
            }).show();
        };
    wemjq(document).on("mouseenter", "*[title]", function (e) {
        wemjq(this).data(DATA, wemjq(this).attr("title"));
        wemjq(this).removeAttr("title").addClass(CLS_ON);
        wemjq("<div id='" + ID + "' />").appendTo("body");
        if (e.pageX) {
            pageX = e.pageX;
        }
        if (e.pageY) {
            pageY = e.pageY;
        }
        showAt(e);
    });
    wemjq(document).on("mouseleave click", "." + CLS_ON, function (e) {
        if (wemjq(this).data(DATA)) {
            wemjq(this).attr("title", wemjq(this).data(DATA));
        }
        wemjq(this).removeClass(CLS_ON);
        wemjq("#" + ID).remove();
    });
    if (FOLLOW) { wemjq(document).on("mousemove", "." + CLS_ON, showAt); }
}

function updateTabTitle(title: string) {
    wemjq('title').html(`${title} - Enonic XP Admin`);
}

function startApplication() {

    let application: api.app.Application = getApplication();

    let wizardParams = ContentWizardPanelParams.fromApp(application);
    if (wizardParams) {
        startContentWizard(wizardParams);
    } else {
        startContentApplication(application);
    }

    initToolTip();

    api.util.AppHelper.preventDragRedirect();

    let contentDeleteDialog = new ContentDeleteDialog();
    ContentDeletePromptEvent.on((event) => {
        contentDeleteDialog
            .setContentToDelete(event.getModels())
            .setYesCallback(event.getYesCallback())
            .setNoCallback(event.getNoCallback())
            .open();
    });

    let contentPublishDialog = new ContentPublishDialog();
    ContentPublishPromptEvent.on((event) => {
        contentPublishDialog
            .setContentToPublish(event.getModels())
            .open();

        if (event.isIncludeChildItems()) {
            contentPublishDialog.setIncludeChildItems(event.isIncludeChildItems());
        }
    });

    let contentUnpublishDialog = new ContentUnpublishDialog();
    ContentUnpublishPromptEvent.on((event) => {
        contentUnpublishDialog
            .setContentToUnpublish(event.getModels())
            .open();
    });

    let editPermissionsDialog = new EditPermissionsDialog();

    application.setLoaded(true);

    this.serverEventsListener.start();
    this.clientEventsListener.start();
    this.lostConnectionDetector.startPolling();

    api.content.event.ContentServerEventsHandler.getInstance().start();
}

function startContentWizard(wizardParams: ContentWizardPanelParams) {
    let wizard = new ContentWizardPanel(wizardParams);

    wizard.onDataLoaded(content => {
        let contentType = (<ContentWizardPanel>wizard).getContentType();
        updateTabTitle(content.getDisplayName() || api.content.ContentUnnamed.prettifyUnnamed(contentType.getDisplayName()));
    });
    wizard.onWizardHeaderCreated(() => {
        // header will be ready after rendering is complete
        wizard.getWizardHeader().onPropertyChanged((event: api.PropertyChangedEvent) => {
            if (event.getPropertyName() === "displayName") {
                let contentType = (<ContentWizardPanel>wizard).getContentType(),
                    name = <string>event.getNewValue() || api.content.ContentUnnamed.prettifyUnnamed(contentType.getDisplayName());

                updateTabTitle(name);
            }
        });
    });

    wizard.onClosed(event => window.close());

    api.dom.WindowDOM.get().onBeforeUnload((event) => {
        if (wizard.isContentDeleted()) {
            return;
        }
        if (wizard.hasUnsavedChanges()) {
            let message = 'Wizard has unsaved changes. Continue without saving ?';
            (event || window.event)['returnValue'] = message;
            return message;
        } else {
            // do close to notify everybody
            wizard.close(false);
        }
    });

    api.dom.Body.get().addClass('wizard-page').appendChild(wizard);
}

function startContentApplication(application: api.app.Application) {
    let body = api.dom.Body.get(),
        appBar = new api.app.bar.AppBar(application),
        appPanel = new ContentAppPanel(appBar, application.getPath());

    this.clientEventsListener.setContentApp(appPanel);

    body.appendChild(appBar);
    body.appendChild(appPanel);

    let newContentDialog = new NewContentDialog();
    ShowNewContentDialogEvent.on((event) => {

        let parentContent: api.content.ContentSummary = event.getParentContent()
            ? event.getParentContent().getContentSummary() : null;

        if (parentContent != null) {
            new api.content.resource.GetContentByIdRequest(parentContent.getContentId()).sendAndParse().then(
                (newParentContent: api.content.Content) => {

                    // TODO: remove pyramid of doom
                    if (parentContent.hasParent() && parentContent.getType().isTemplateFolder()) {
                        new api.content.resource.GetContentByPathRequest(parentContent.getPath().getParentPath()).sendAndParse().then(
                            (grandParent: api.content.Content) => {

                                newContentDialog.setParentContent(newParentContent);
                                newContentDialog.open();
                            }).catch((reason: any) => {
                            api.DefaultErrorHandler.handle(reason);
                        }).done();
                    }
                    else {
                        newContentDialog.setParentContent(newParentContent);
                        newContentDialog.open();
                    }
                }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();
        }
        else {
            newContentDialog.setParentContent(null);
            newContentDialog.open();
        }
    });

    let sortDialog = new SortContentDialog();
    let moveDialog = new MoveContentDialog();

    window.onmessage = (e: MessageEvent) => {
        if (e.data.appLauncherEvent) {
            let eventType: api.app.AppLauncherEventType = api.app.AppLauncherEventType[<string>e.data.appLauncherEvent];
            if (eventType == api.app.AppLauncherEventType.Show) {
                appPanel.activateCurrentKeyBindings();
            }
        }
    };
}

window.onload = function () {
    startApplication();
};
