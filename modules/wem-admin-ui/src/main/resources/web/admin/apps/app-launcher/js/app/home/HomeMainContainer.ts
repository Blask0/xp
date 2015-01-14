module app.home {

    export class HomeMainContainer extends api.dom.DivEl {

        private brandingPanel: Branding;

        private appSelector: app.launcher.AppSelector;

        private loginForm: app.login.LoginForm;

        private linksContainer: LinksContainer;

        private centerPanel: CenterPanel;

        private backgroundImgUrl: string;

        private headerPanel: HeaderPanel;

        constructor(builder: HomeMainContainerBuilder) {
            super('home-main-container');

            this.getEl().setAttribute("tabindex", "100"); //Need tabindex to be able to focus element

            this.appSelector = builder.appSelector;
            this.loginForm = builder.loginForm;
            this.linksContainer = builder.linksContainer;
            this.backgroundImgUrl = builder.backgroundImgUrl;

            this.setBackgroundImgUrl(this.backgroundImgUrl);

            this.headerPanel = new HeaderPanel();

            this.brandingPanel = new Branding();

            this.centerPanel = new CenterPanel();
            this.centerPanel.prependChild(this.headerPanel);
            this.centerPanel.prependChild(this.brandingPanel);

            this.centerPanel.addToAppSelectorPanel(this.appSelector);

            this.centerPanel.addToLoginPanel(this.loginForm);
            this.centerPanel.addToLoginPanel(this.linksContainer);

            this.appendChild(this.centerPanel);

            LogOutEvent.on(() => {
                new api.security.auth.LogoutRequest().sendAndParse().then(() => {
                    this.centerPanel.showLoginPanel();
                    this.setBackgroundImgUrl(this.backgroundImgUrl);
                    this.headerPanel.disableReturnButton();
                    this.brandingPanel.show();
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).done();
            });

        }

        setReturnAction(action: api.ui.Action) {
            this.headerPanel.setReturnAction(action);
        }

        showLogin() {
            this.centerPanel.showLoginPanel();
        }

        showAppSelector() {
            this.centerPanel.showAppSelectorPanel();
        }

        show() {
            this.appSelector.showAppsCount();
            super.show();
        }

        hide() {
            api.ui.KeyBindings.get().unbindKeys(this.appSelector.getKeyBindings());
            super.hide();
        }

        enableReturnButton() {
            this.headerPanel.enableReturnButton();
        }

        disableBranding() {
            this.brandingPanel.hide();
        }
    }

    export class HomeMainContainerBuilder {

        backgroundImgUrl: string;

        appSelector: app.launcher.AppSelector;

        loginForm: app.login.LoginForm;

        linksContainer: app.home.LinksContainer;

        setBackgroundImgUrl(value: string): HomeMainContainerBuilder {
            this.backgroundImgUrl = value;
            return this;
        }

        setAppSelector(value: app.launcher.AppSelector): HomeMainContainerBuilder {
            this.appSelector = value;
            return this;
        }

        setLoginForm(value: app.login.LoginForm): HomeMainContainerBuilder {
            this.loginForm = value;
            return this;
        }

        setLinksContainer(value: LinksContainer): HomeMainContainerBuilder {
            this.linksContainer = value;
            return this;
        }

        build(): HomeMainContainer {
            return new HomeMainContainer(this);
        }
    }

}
