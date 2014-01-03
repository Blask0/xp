module api.app.browse {

    export class ItemsSelectionPanel<M> extends api.ui.Panel implements api.event.Observable {

        private listeners:ItemsSelectionPanelListener<M>[] = [];
        private items:BrowseItem<M>[] = [];
        private selectionItems:SelectionItem<M>[] = [];

        constructor() {
            super("ItemsSelectionPanel");
            this.getEl().setInnerHtml("Nothing selected");
        }

        setItems(items:BrowseItem<M>[]) {
            var itemsToRemove = this.items.filter((item:BrowseItem<M>) => {
                for (var i = 0 ; i < items.length ; i++) {
                    if (item.getPath() == items[i].getPath()) {
                        return false;
                    }
                }
                return true;
            });
            itemsToRemove.forEach((item:BrowseItem<M>) => {
                this.removeItem(item);
            });

            items.forEach((item:BrowseItem<M>) => {
                this.addItem(item);
            });
        }

        addItem(item:BrowseItem<M>) {
            if (this.indexOf(item) >= 0) {
                return;
            }

            if (this.items.length == 0) {
                this.removeChildren();
            }

            var removeCallback = () => {
                this.removeItem(item);
            };
            var selectionItem = new SelectionItem(item, removeCallback);

            this.appendChild(selectionItem);
            this.selectionItems.push(selectionItem);
            this.items.push(item);
        }

        removeItem(item:BrowseItem<M>) {
            var index = this.indexOf(item);
            if (index < 0) {
                return;
            }

            this.selectionItems[index].remove();
            this.selectionItems.splice(index, 1);
            this.items.splice(index, 1);

            if (this.items.length == 0) {
                this.getEl().setInnerHtml("Nothing selected");
            }

            this.notifyDeselected(item);
        }

        getItems():BrowseItem<M>[] {
            return this.items;
        }

        private indexOf(item:BrowseItem<M>):number {
            for (var i = 0 ; i < this.items.length ; i++) {
                if (item.getPath() == this.items[i].getPath()) {
                    return i;
                }
            }
            return -1;
        }

        addListener(listener:ItemsSelectionPanelListener<M>) {
            this.listeners.push(listener);
        }

        removeListener(listener:ItemsSelectionPanelListener<M>) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyDeselected(item:BrowseItem<M>) {
            this.listeners.forEach((listener:ItemsSelectionPanelListener<M>) => {
                if (listener.onDeselected) {
                    listener.onDeselected(item);
                }
            });
        }

    }

    export class SelectionItem<M> extends api.dom.DivEl {

        private browseItem:api.app.browse.BrowseItem<M>;

        constructor(browseItem:BrowseItem<M>, removeCallback?:() => void) {
            super("SelectionItem", "browse-selection-item");
            this.browseItem = browseItem;
            this.setIcon(this.browseItem.getIconUrl(), 32);
            this.setData(this.browseItem.getDisplayName(), this.browseItem.getPath());
            this.addRemoveButton(removeCallback);
        }

        private addRemoveButton(callback?:() => void) {
            var removeEl = document.createElement("div");
            removeEl.className = "remove";
            removeEl.innerHTML = "&times;";
            removeEl.addEventListener("click", (event) => {
                if (callback) {
                    callback();
                }
            });
            this.getEl().appendChild(removeEl);
        }

        private setIcon(iconUrl:string, size:number) {
            this.getEl().appendChild(api.util.ImageLoader.get(iconUrl + "?size=" + size, 32, 32));
        }

        private setData(title:string, subtitle:string) {
            var titleEl = document.createElement("h6");
            titleEl.innerHTML = title;

            var subtitleEl = document.createElement("small");
            subtitleEl.innerHTML = subtitle;
            titleEl.appendChild(subtitleEl);

            this.getEl().appendChild(titleEl);
            return titleEl;
        }

        getBrowseItem():BrowseItem<M> {
            return this.browseItem;
        }
    }

}
