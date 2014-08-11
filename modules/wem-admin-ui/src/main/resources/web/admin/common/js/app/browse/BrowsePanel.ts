module api.app.browse {

    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveRanges = api.ui.responsive.ResponsiveRanges;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;

    export interface BrowsePanelParams<M> {

        browseToolbar:api.ui.toolbar.Toolbar;

        treeGridPanel2?:api.ui.treegrid.TreeGrid<Object>;

        browseItemPanel:BrowseItemPanel<M>;

        filterPanel?:api.app.browse.filter.BrowseFilterPanel;
    }

    export class BrowsePanel<M> extends api.ui.panel.Panel implements api.ui.ActionContainer {

        private static SPLIT_PANEL_ALIGNMENT_TRESHOLD: number = 1180;

        private browseToolbar: api.ui.toolbar.Toolbar;

        private newTreeGrid: api.ui.treegrid.TreeGrid<Object>;

        private browseItemPanel: BrowseItemPanel<M>;

        private gridAndDetailSplitPanel: api.ui.panel.SplitPanel;

        private filterPanel: api.app.browse.filter.BrowseFilterPanel;

        private filterAndGridAndDetailSplitPanel: api.ui.panel.SplitPanel;

        private gridAndToolbarContainer: api.ui.panel.Panel;

        private refreshNeeded: boolean = false;

        private filterPanelForcedShown: boolean = false;

        constructor(params: BrowsePanelParams<M>) {
            super();

            this.browseToolbar = params.browseToolbar;
            this.newTreeGrid = params.treeGridPanel2;
            this.browseItemPanel = params.browseItemPanel;
            this.filterPanel = params.filterPanel;

            this.browseItemPanel.onDeselected((event: ItemDeselectedEvent<M>) => {
                this.newTreeGrid.deselectNode(event.getBrowseItem().getId());
            });

            this.gridAndToolbarContainer = new api.ui.panel.Panel();
            this.gridAndToolbarContainer.appendChild(this.browseToolbar);

            var gridPanel = new api.ui.panel.Panel();
            gridPanel.appendChild(this.newTreeGrid);

            this.gridAndToolbarContainer.appendChild(gridPanel);

            this.gridAndDetailSplitPanel = new api.ui.panel.SplitPanelBuilder(this.gridAndToolbarContainer, this.browseItemPanel)
                .setAlignmentTreshold(BrowsePanel.SPLIT_PANEL_ALIGNMENT_TRESHOLD).build();

            if (this.filterPanel) {
                this.filterAndGridAndDetailSplitPanel = new api.ui.panel.SplitPanelBuilder(this.filterPanel, this.gridAndDetailSplitPanel)
                    .setFirstPanelSize(200,
                    api.ui.panel.SplitPanelUnit.PIXEL).setAlignment(api.ui.panel.SplitPanelAlignment.VERTICAL).build();
            } else {
                this.filterAndGridAndDetailSplitPanel = this.gridAndDetailSplitPanel;
            }

            this.newTreeGrid.onRowSelectionChanged((nodes: api.ui.treegrid.TreeNode<Object>[]) => {
                var browseItems: api.app.browse.BrowseItem<M>[] = this.treeNodesToBrowseItems(nodes);
                this.browseItemPanel.setItems(browseItems);
            });

            this.onRendered((event) => {
                this.appendChild(this.filterAndGridAndDetailSplitPanel);
            });

            ResponsiveManager.onAvailableSizeChanged(this, (item: ResponsiveItem) => {
                if (item.isInRangeOrSmaller(ResponsiveRanges._360_540)) {
                    if (this.filterPanel && !this.filterAndGridAndDetailSplitPanel.isPanelHidden(1) && !this.filterPanelForcedShown) {
                        this.filterAndGridAndDetailSplitPanel.hidePanel(1);
                    }
                    if (!this.gridAndDetailSplitPanel.isPanelHidden(2)) {
                        this.gridAndDetailSplitPanel.hidePanel(2);
                    }
                } else if (item.isInRangeOrBigger(ResponsiveRanges._540_720)) {
                    if (this.filterPanel && this.filterAndGridAndDetailSplitPanel.isPanelHidden(1)) {
                        this.filterAndGridAndDetailSplitPanel.showPanel(1);
                    }
                    if (this.gridAndDetailSplitPanel.isPanelHidden(2)) {
                        this.gridAndDetailSplitPanel.showPanel(2);
                    }
                }
            });
        }

        getActions(): api.ui.Action[] {
            return this.browseToolbar.getActions();
        }

        // TODO: ContentSummary must be replaced with an ContentSummaryAndCompareStatus after old grid is removed
        treeNodesToBrowseItems(nodes: api.ui.treegrid.TreeNode<Object>[]): BrowseItem<M>[] {
            return [];
        }

        refreshFilterAndGrid() {
            if (this.isRefreshNeeded()) {
                // do the search to update facets as well as the grid
                if (this.filterPanel) {
                    this.filterPanel.search();
                } else {
                    this.newTreeGrid.reload();
                }
                this.refreshNeeded = false;
            }
        }

        isRefreshNeeded(): boolean {
            return this.refreshNeeded;
        }

        setRefreshNeeded(refreshNeeded: boolean) {
            this.refreshNeeded = refreshNeeded;
        }

        toggleFilterPanel() {
            this.filterPanelForcedShown = !this.filterPanelForcedShown;
            !this.filterAndGridAndDetailSplitPanel.isPanelHidden(1)
                ? this.filterAndGridAndDetailSplitPanel.hidePanel(1)
                : this.filterAndGridAndDetailSplitPanel.showPanel(1);
        }

    }
}
