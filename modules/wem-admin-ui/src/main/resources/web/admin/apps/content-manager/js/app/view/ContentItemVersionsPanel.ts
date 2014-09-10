module app.view {

    import ViewItem = api.app.view.ViewItem;
    import ContentSummary = api.content.ContentSummary;

    export class ContentItemVersionsPanel extends api.ui.panel.Panel {

        private item: ViewItem<ContentSummary>;

        private deckPanel: api.ui.panel.NavigatedDeckPanel;
        private activeGrid: ContentVersionsTreeGrid;
        private allGrid: ContentVersionsTreeGrid;
        private mask: api.ui.mask.LoadMask;

        constructor() {
            super("item-versions-panel");

            var navigator = new api.ui.tab.TabBar();
            this.deckPanel = new api.ui.panel.NavigatedDeckPanel(navigator);
            this.appendChild(navigator);
            this.appendChild(this.deckPanel);
            this.mask = new api.ui.mask.LoadMask(this);
            this.appendChild(this.mask);

            navigator.onNavigationItemSelected((event: api.ui.NavigatorEvent) => {
                this.setItem(this.item);
            });

            this.activeGrid = new ActiveContentVersionsTreeGrid();
            this.activeGrid.onLoaded(() => {
                this.mask.hide();
            });
            this.deckPanel.addNavigablePanel(new api.ui.tab.TabBarItem('Active Versions'), this.activeGrid, true);

            this.allGrid = new AllContentVersionsTreeGrid();
            this.allGrid.onLoaded(() => {
                this.mask.hide();
            });
            this.deckPanel.addNavigablePanel(new api.ui.tab.TabBarItem('All Versions'), this.allGrid);
        }

        public setItem(item: ViewItem<ContentSummary>) {
            this.item = item;
            if (this.item) {
                var panel = <ContentVersionsTreeGrid>this.deckPanel.getPanelShown();
                if (panel.getContentId() != this.item.getModel().getContentId()) {
                    this.mask.show();
                    (<ContentVersionsTreeGrid>this.deckPanel.getPanelShown()).setContentId(item.getModel().getContentId());
                }
            }
        }

        public getItem(): ViewItem<ContentSummary> {
            return this.item;
        }
    }

}
