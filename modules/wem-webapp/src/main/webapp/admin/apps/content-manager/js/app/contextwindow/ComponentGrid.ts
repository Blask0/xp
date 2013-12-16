module app_contextwindow {
    export interface ComponentGridOptions {
        draggableRows?:boolean;
        rowClass?:string;
        onClick?:(el) => void;
    }

    export class ComponentGrid extends api_ui_grid.Grid<ComponentData> {

        private componentGridOptions:ComponentGridOptions;

        private componentDataView:api_ui_grid.DataView<ComponentData>;

        constructor(dataView:api_ui_grid.DataView<ComponentData>, options:ComponentGridOptions = {}) {
            super(dataView, this.createColumns(), {hideColumnHeaders: true, rowHeight: 50, height: 400, width: 320});
            this.componentDataView = dataView;
            this.componentGridOptions = options;
            this.setFilter(this.filter);
        }

        afterRender() {
            super.afterRender();
            if (this.componentGridOptions.onClick) {
                this.setOnClick(this.componentGridOptions.onClick);
            }
        }

        private filter(item, args) {
            if (args) {
                if (args.searchString != "" && item["component"]["name"].indexOf(args.searchString) == -1) {
                    return false;
                }
            }

            return true;
        }

        updateFilter(searchString:string) {
            this.componentDataView.setFilterArgs({
                searchString: searchString
            });
            this.componentDataView.refresh();
        }

        private createColumns():api_ui_grid.GridColumn<ComponentData>[] {
            return [
                {
                    name: "component",
                    field: "component",
                    id: "component",
                    width: 320,
                    cssClass: "grid-row",
                    formatter: (row, cell, value, columnDef, dataContext) => {
                        return this.buildRow(row, cell, value).toString();
                    }
                }
            ];
        }

        private buildRow(row, cell, data):api_dom.DivEl {
            var rowEl = new api_dom.DivEl();
            rowEl.getEl().setData('live-edit-key', data.key);
            rowEl.getEl().setData('live-edit-name', data.name);
            rowEl.getEl().setData('live-edit-type', data.typeName);
            if (this.componentGridOptions.draggableRows) {
                rowEl.getEl().setData('context-window-draggable', 'true');
            }
            if (this.componentGridOptions.rowClass) {
                rowEl.addClass(this.componentGridOptions.rowClass)
            }

            var icon = new api_dom.DivEl();
            icon.setClass('live-edit-font-icon-' + data.typeName);
            icon.addClass('icon');

            var title = new api_dom.H5El();
            title.getEl().setInnerHtml(data.name);

            var subtitle = new api_dom.H6El();
            subtitle.getEl().setInnerHtml(data.subtitle);

            rowEl.appendChild(icon);
            rowEl.appendChild(title);
            rowEl.appendChild(subtitle);

            return rowEl;
        }

        static toSlickData(data:any[]):any[] {
            var result = [];
            data["components"].forEach((item, index) => {
                var tmp = {
                    "id": item.key,
                    "component": item
                };
                result.push(tmp);
            });
            return result;
        }
    }


}