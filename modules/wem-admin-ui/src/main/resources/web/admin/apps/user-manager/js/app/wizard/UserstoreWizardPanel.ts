module app.wizard {

    import UserStore = api.security.UserStore;
    import UserStoreKey = api.security.UserStoreKey;
    import UserStoreNamedEvent = api.security.UserStoreNamedEvent;
    import CreateUserStoreRequest = api.security.CreateUserStoreRequest;
    import UpdateUserStoreRequest = api.security.UpdateUserStoreRequest;
    import UserStoreBuilder = api.security.UserStoreBuilder;

    import ConfirmationDialog = api.ui.dialog.ConfirmationDialog;
    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;
    import WizardStep = api.app.wizard.WizardStep;
    import FormIcon = api.app.wizard.FormIcon;
    import WizardHeaderWithDisplayNameAndName = api.app.wizard.WizardHeaderWithDisplayNameAndName;
    import WizardHeaderWithDisplayNameAndNameBuilder = api.app.wizard.WizardHeaderWithDisplayNameAndNameBuilder;

    export class UserStoreWizardPanel extends UserItemWizardPanel<UserStore> {

        private descriptionWizardStepForm: PrincipalDescriptionWizardStepForm;

        private permissionsWizardStepForm: SecurityWizardStepForm;

        private userStore: UserStoreKey;

        isUserStoreFormValid: boolean;
        userStorePath: string;

        userStoreNamedListeners: {(event: UserStoreNamedEvent): void}[];

        constructor(params: UserStoreWizardPanelParams, callback: (wizard: UserStoreWizardPanel) => void) {

            this.descriptionWizardStepForm = new PrincipalDescriptionWizardStepForm();
            this.permissionsWizardStepForm = new SecurityWizardStepForm();

            this.constructing = true;
            this.isUserStoreFormValid = false;
            this.userStoreNamedListeners = [];

            this.userStorePath = params.persistedPath;

            var iconUrl = api.dom.ImgEl.PLACEHOLDER;
            this.formIcon = new FormIcon(iconUrl, "Click to upload icon");
            this.formIcon.addClass("icon icon-xlarge");
            this.formIcon.addClass("icon-shield");

            this.wizardActions = new app.wizard.action.UserStoreWizardActions(this);
            this.toolbar = new UserStoreWizardToolbar({
                saveAction: this.wizardActions.getSaveAction(),
                deleteAction: this.wizardActions.getDeleteAction(),
                closeAction: this.wizardActions.getCloseAction()
            });

            this.wizardHeader = new WizardHeaderWithDisplayNameAndNameBuilder().build();

            this.wizardHeader.setPath(this.userStorePath);

            if (params.userStore) {
                this.wizardHeader.disableNameInput();
                this.wizardHeader.setAutoGenerationEnabled(false);
            } else {
                this.getUserStoreWizardHeader().onPropertyChanged((event: api.PropertyChangedEvent) => {
                    if (event.getPropertyName() === "name") {
                        this.wizardActions.getSaveAction().setEnabled(!!event.getNewValue());
                    }
                });
            }

            super(params, () => {

                this.addClass("principal-wizard-panel");

                var responsiveItem = ResponsiveManager.onAvailableSizeChanged(this, (item: ResponsiveItem) => {
                    if (this.isVisible()) {
                        this.updateStickyToolbar();
                    }
                });

                this.onRemoved((event) => {
                    ResponsiveManager.unAvailableSizeChanged(this);
                });

                this.onShown((event: api.dom.ElementShownEvent) => {
                    if (this.getPersistedItem()) {
                        app.Router.setHash("edit/" + this.getPersistedItem().getKey());
                    } else {
                        app.Router.setHash("new/");
                    }

                    responsiveItem.update();
                });

                this.constructing = false;

                callback(this);
            });
        }

        getUserStoreWizardHeader(): WizardHeaderWithDisplayNameAndName {
            return this.wizardHeader;
        }

        giveInitialFocus() {
            var newWithoutDisplayCameScript = this.isLayingOutNew();

            if (newWithoutDisplayCameScript) {
                this.wizardHeader.giveFocus();
            } else if (!this.wizardHeader.giveFocus()) {
                this.wizardHeader.giveFocus();
            }

            this.startRememberFocus();
        }

        saveChanges(): wemQ.Promise<UserStore> {
            if (!this.wizardHeader.getName()) {
                var deferred = wemQ.defer<UserStore>();
                api.notify.showError("Name can not be empty");
                // deferred.resolve(null);
                deferred.reject(new Error("Name can not be empty"));
                return deferred.promise;
            } else {
                return super.saveChanges();
            }

        }

        createSteps(): wemQ.Promise<any[]> {
            var deferred = wemQ.defer<WizardStep[]>();

            var steps: WizardStep[] = [];

            steps.push(new WizardStep("UserStore", this.descriptionWizardStepForm));
            steps.push(new WizardStep("Permissions", this.permissionsWizardStepForm));

            this.setSteps(steps);

            deferred.resolve(steps);
            return deferred.promise;
        }

        preLayoutNew(): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();

            this.doLayoutPersistedItem(null);

            deferred.resolve(null);

            return deferred.promise;
        }

        postLayoutNew(): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();

            this.wizardHeader.initNames("", this.userStorePath, false);

            deferred.resolve(null);
            return deferred.promise;
        }

        layoutPersistedItem(persistedUserStore: UserStore): wemQ.Promise<void> {

            var viewedUserStore;
            if (!this.constructing) {

                var deferred = wemQ.defer<void>();
                deferred.resolve(null);
                return deferred.promise;
            }
            else {
                return this.doLayoutPersistedItem(persistedUserStore.clone());
            }
        }

        doLayoutPersistedItem(userStore: UserStore): wemQ.Promise<void> {

            var parallelPromises: wemQ.Promise<any>[] = [
                // Load attachments?
                this.createSteps()
            ];

            return wemQ.all(parallelPromises).
                spread<void>(() => {
            });
        }

        postLayoutPersisted(existing: UserStore): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();

            this.wizardHeader.initNames(existing.getDisplayName(), existing.getKey().getId(), false);
            this.permissionsWizardStepForm.layout(existing);

            deferred.resolve(null);
            return deferred.promise;
        }

        persistNewItem(): wemQ.Promise<UserStore> {
            return this.produceCreateUserStoreRequest().sendAndParse().
                then((userStore: UserStore) => {
                    this.wizardHeader.disableNameInput();
                    this.wizardHeader.setAutoGenerationEnabled(false);
                    api.notify.showFeedback('UserStore was created!');
                    return userStore;
                });
        }

        updatePersistedItem(): wemQ.Promise<UserStore> {
            return this.produceUpdateUserStoreRequest(this.assembleViewedUserStore()).
                sendAndParse().
                then((userStore: UserStore) => {
                    if (!this.getPersistedItem().getDisplayName() && !!userStore.getDisplayName()) {
                        this.notifyUserStoreNamed(userStore);
                    }
                    api.notify.showFeedback('UserStore was updated!');

                    return userStore;
                });
        }


        hasUnsavedChanges(): boolean {
            var persistedUserStore: UserStore = this.getPersistedItem();
            if (persistedUserStore == undefined) {
                return true;
            } else {
                var viewedUserStore = this.assembleViewedUserStore();
                return !viewedUserStore.equals(this.getPersistedItem());
            }
        }

        resolveUserStoreNameForUpdateRequest(): string {
            if (api.util.StringHelper.isEmpty(this.wizardHeader.getName())) {
                return this.getPersistedItem().getDisplayName();
            } else {
                return this.wizardHeader.getName();
            }
        }

        private assembleViewedUserStore(): UserStore {
            return new UserStoreBuilder().
                setDisplayName(this.wizardHeader.getDisplayName()).
                setKey(this.getPersistedItem().getKey().toString()).
                setPermissions(this.permissionsWizardStepForm.getPermissions()).
                build();
        }

        private produceCreateUserStoreRequest(): CreateUserStoreRequest {
            var key = new UserStoreKey(this.wizardHeader.getName()),
                name = this.wizardHeader.getDisplayName(),
                permissions = this.permissionsWizardStepForm.getPermissions();
            return new CreateUserStoreRequest().
                setDisplayName(name).
                setKey(key).
                setPermissions(permissions);
        }

        private produceUpdateUserStoreRequest(viewedUserStore: UserStore): UpdateUserStoreRequest {
            var key = this.getPersistedItem().getKey(),
                name = viewedUserStore.getDisplayName(),
                permissions = viewedUserStore.getPermissions();

            return new UpdateUserStoreRequest().
                setKey(key).
                setDisplayName(name).
                setPermissions(permissions);
        }


        onUserStoreNamed(listener: (event: UserStoreNamedEvent)=>void) {
            this.userStoreNamedListeners.push(listener);
        }

        notifyUserStoreNamed(userStore: UserStore) {
            this.userStoreNamedListeners.forEach((listener: (event: UserStoreNamedEvent)=>void)=> {
                listener.call(this, new UserStoreNamedEvent(this, userStore));
            });
        }
    }
}
