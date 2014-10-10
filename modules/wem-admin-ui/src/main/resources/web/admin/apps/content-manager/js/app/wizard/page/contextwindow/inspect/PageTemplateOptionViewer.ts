module app.wizard.page.contextwindow.inspect {

    export class PageTemplateOptionViewer extends api.ui.Viewer<PageTemplateOption> {

        private namesAndIconView: api.app.NamesAndIconView;

        private defaultPageTemplateIconUrl: string;

        private pageTemplateIconUrl: string;

        constructor() {
            super();

            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            this.appendChild(this.namesAndIconView);
        }

        setObject(option: PageTemplateOption) {
            super.setObject(option);

            var pageTemplate = option.getPageTemplate();
            if (pageTemplate) {
                this.namesAndIconView.
                    setMainName(pageTemplate.getDisplayName()).
                    setSubName(pageTemplate.getController().toString()).
                    setIconClass("icon-newspaper icon-large");
            }
            else {
                this.namesAndIconView.
                    setMainName("Auto").
                    setSubName("Page Template automatically chosen").
                    setIconClass("icon-wand icon-large");
            }
        }

        getPreferredHeight(): number {
            return 50;
        }
    }
}