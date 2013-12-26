module api_app {

    export interface BrowseBasedAppPanelConfig<M> {

        appBar:api_app.AppBar;

        browsePanel:api_app_browse.BrowsePanel<M>;
    }

    export class BrowseAndWizardBasedAppPanel<M> extends api_app.AppPanel {

        private browsePanel:api_app_browse.BrowsePanel<M>;

        private appBarTabMenu:api_app.AppBarTabMenu;

        private currentKeyBindings:api_ui.KeyBinding[];

        constructor(config:BrowseBasedAppPanelConfig<M>) {
            super(config.appBar.getTabMenu(), config.browsePanel);

            this.browsePanel = config.browsePanel;
            this.appBarTabMenu = config.appBar.getTabMenu();

            this.currentKeyBindings = api_ui.Action.getKeyBindings(this.resolveActions(this.browsePanel));
            this.activateCurrentKeyBindings();

            this.addListener({
                onPanelShown: (event:api_ui.PanelShownEvent) => {
                    if (event.panel === this.browsePanel) {
                        this.browsePanel.refreshFilterAndGrid();
                    }

                    var previousActions = this.resolveActions(event.previousPanel);
                    api_ui.KeyBindings.get().unbindKeys(api_ui.Action.getKeyBindings(previousActions));

                    var nextActions = this.resolveActions(event.panel);
                    this.currentKeyBindings = api_ui.Action.getKeyBindings(nextActions);
                    api_ui.KeyBindings.get().bindKeys(this.currentKeyBindings);
                }
            });
        }

        activateCurrentKeyBindings() {

            if( this.currentKeyBindings ) {
                api_ui.KeyBindings.get().bindKeys(this.currentKeyBindings);
            }
        }

        getAppBarTabMenu():api_app.AppBarTabMenu {
            return this.appBarTabMenu;
        }

        addViewPanel(tabMenuItem:AppBarTabMenuItem, viewPanel:api_app_view.ItemViewPanel<M>) {
            super.addNavigablePanelToFront(tabMenuItem, viewPanel);

            tabMenuItem.addListener({
                onClose: (tab: AppBarTabMenuItem) => {
                    viewPanel.close();
                }
            });

            viewPanel.addListener({
                onClosed: (view) => {
                    this.removePanel(view, false);
                }
            });
        }

        addWizardPanel(tabMenuItem:AppBarTabMenuItem, wizardPanel:api_app_wizard.WizardPanel<any>) {
            super.addNavigablePanelToFront(tabMenuItem, wizardPanel);

            tabMenuItem.addListener({
                onClose: (tab: AppBarTabMenuItem) => {
                    wizardPanel.close();
                }
            });

            wizardPanel.addListener({
                onClosed: (wizard) => {
                    this.removePanel(wizard, false);
                }
            });
        }

        canRemovePanel(panel:api_ui.Panel):boolean {
            if (panel instanceof api_app_wizard.WizardPanel) {
                var wizardPanel:api_app_wizard.WizardPanel<any> = <api_app_wizard.WizardPanel<any>>panel;
                return wizardPanel.canClose();
            }
            return true;
        }

        private resolveActions(panel:api_ui.Panel):api_ui.Action[] {

            if (panel instanceof api_app_wizard.WizardPanel || panel instanceof api_app_browse.BrowsePanel) {
                var actionContainer:api_ui.ActionContainer = <any>panel;
                return actionContainer.getActions();
            }
            else {
                return [];
            }
        }
    }
}
