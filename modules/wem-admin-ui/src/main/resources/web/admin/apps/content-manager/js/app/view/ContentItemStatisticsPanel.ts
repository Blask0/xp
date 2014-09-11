module app.view {

    import Panel = api.ui.panel.Panel;
    import TabMenuItemBuilder = api.ui.tab.TabMenuItemBuilder;

    export class ContentItemStatisticsPanel extends api.app.view.ItemStatisticsPanel<api.content.ContentSummary> {

        private previewPanel: ContentItemPreviewPanel;

        private analyticsPanel: ContentItemAnalyticsPanel;

        private versionsPanel: ContentItemVersionsPanel;

        constructor() {
            super();

            this.previewPanel = new ContentItemPreviewPanel();
            this.addNavigablePanel(new TabMenuItemBuilder().setLabel("Preview").build(), this.previewPanel, true);

            this.analyticsPanel = new ContentItemAnalyticsPanel();
            this.addNavigablePanel(new TabMenuItemBuilder().setLabel("Google Analytics").build(), this.analyticsPanel);

            this.addNavigablePanel(new TabMenuItemBuilder().setLabel("Details").build(), new Panel());
            this.addNavigablePanel(new TabMenuItemBuilder().setLabel("Relationships").build(), new Panel());

            this.versionsPanel = new ContentItemVersionsPanel();
            this.addNavigablePanel(new TabMenuItemBuilder().setLabel("Version History").build(), this.versionsPanel);
            this.addNavigablePanel(new TabMenuItemBuilder().setLabel("SEO").build(), new Panel());

            this.getTabMenu().onNavigationItemSelected((event: api.ui.NavigatorEvent) => {
                this.onTabSelected(event.getItem());
            });
        }

        setItem(item: api.app.view.ViewItem<api.content.ContentSummary>) {
            if (this.getItem() != item) {
                super.setItem(item);
                switch (this.getTabMenu().getSelectedIndex()) {
                case 0:
                    this.previewPanel.setItem(item);
                    break;
                case 1:
                    this.analyticsPanel.setItem(item);
                    break;
                case 4:
                    this.versionsPanel.setItem(item);
                    break;
                }
            }
        }

        private onTabSelected(navigationItem: api.ui.NavigationItem) {
            var item = this.getItem();
            switch (navigationItem.getIndex()) {
            case 0:
                this.getHeader().hide();
                if (this.previewPanel.getItem() != item) {
                    this.previewPanel.setItem(item);
                }
                break;
            case 1:
                this.getHeader().hide();
                if (this.analyticsPanel.getItem() != item) {
                    this.analyticsPanel.setItem(item);
                }
                break;
            case 4:
                this.getHeader().hide();
                if (this.versionsPanel.getItem() != item) {
                    this.versionsPanel.setItem(item);
                }
                break;
            default:
                this.getHeader().show();
            }

        }

    }

}
