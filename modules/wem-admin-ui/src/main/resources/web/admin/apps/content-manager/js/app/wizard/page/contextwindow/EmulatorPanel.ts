module app.wizard.page.contextwindow {

    export interface EmulatorPanelConfig {

        liveEditPage: app.wizard.page.LiveEditPageProxy;
    }

    export class EmulatorPanel extends api.ui.Panel {

        private dataView: api.ui.grid.DataView<any>;
        private grid: EmulatorGrid;

        private liveEditPage: app.wizard.page.LiveEditPageProxy;

        constructor(config: EmulatorPanelConfig) {
            super("emulator-panel");

            this.liveEditPage = config.liveEditPage;

            var text = new api.dom.PEl();
            text.getEl().setInnerHtml("Emulate different client's physical sizes");
            this.appendChild(text);

            this.dataView = new api.ui.grid.DataView<any>();
            this.grid = new EmulatorGrid(this.dataView);
            this.appendChild(this.grid);

            this.getData();

            // Using jQuery since grid.setOnClick fires event twice, bug in slickgrid
            jQuery(this.getHTMLElement()).on("click", ".grid-row", (event: JQueryEventObject) => {

                var width = jQuery(event.currentTarget).children('div').data("width");
                var height = jQuery(event.currentTarget).children('div').data("height");
                var type = jQuery(event.currentTarget).children('div').data("device.type");

                this.liveEditPage.setWidth(width);
                this.liveEditPage.setHeight(height);
            });

            jQuery(this.getHTMLElement()).on("click", ".rotate", (event: JQueryEventObject) => {

                event.stopPropagation();

                this.liveEditPage.setHeightPx(this.liveEditPage.getHeight());
                this.liveEditPage.setWidthPx(this.liveEditPage.getWidth());
            });
        }

        private getData(): void {
            jQuery.ajax({
                url: api.util.getAdminUri("apps/content-manager/js/data/context-window/devices.json"),
                success: (data: any, textStatus: string, jqXHR: JQueryXHR) => {
                    this.dataView.setItems(EmulatorGrid.toSlickData(data));
                    this.grid.setActiveCell(0, 0); // select first option
                }
            });
        }
    }
}