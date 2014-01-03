module app.contextwindow {
    export class InspectorPanel extends api.ui.DeckPanel {
        private detailPanel:DetailPanel;
        private selectPanel:SelectPanel;
        private imageSelectPanel:app.contextwindow.image.ImageSelectPanel;

        constructor(contextWindow:ContextWindow) {
            super();

            this.detailPanel = new DetailPanel(contextWindow);
            this.selectPanel = new SelectPanel(contextWindow);
            this.imageSelectPanel = new app.contextwindow.image.ImageSelectPanel(contextWindow);


            this.addPanel(this.detailPanel);
            this.addPanel(this.selectPanel);
            this.addPanel(this.imageSelectPanel);

            ComponentSelectEvent.on((event) => {
                switch (event.getComponent().componentType.typeName) {
                    case 'image':
                        this.showPanel(this.getPanelIndex(this.imageSelectPanel))
                        break;
                    default:
                        event.getComponent().isEmpty() ? this.showPanel(this.getPanelIndex(this.selectPanel)) : this.showPanel(this.getPanelIndex(this.detailPanel));
                }
            });

        }
    }
}