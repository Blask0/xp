module api.content.page {

    export class PageTemplateSummaryViewer extends api.ui.Viewer<PageTemplateSummary> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            this.namesAndIconView.setIconClass("icon-newspaper icon-large");
            this.appendChild(this.namesAndIconView);
        }

        setObject(pageTemplate: PageTemplateSummary) {
            super.setObject(pageTemplate);
            this.namesAndIconView.setMainName(pageTemplate.getDisplayName()).
                setSubName(pageTemplate.getDescriptorKey().toString());
        }

        getPreferredHeight(): number {
            return 50;
        }
    }
}