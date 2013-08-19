module api_ui {

    export interface DeckPanelListener extends api_ui.Listener {

        onPanelShown?(panel:api_ui.Panel, index:number);

    }

    /**
     * A panel having multiple child panels, but showing only one at a time - like a deck of cards.
     */
    export class DeckPanel extends Panel implements api_ui.Observable {

        private panels:Panel[] = [];

        private panelShown:Panel = null;

        private listeners:DeckPanelListener[] = [];

        constructor(idPrefix?:string) {
            super(idPrefix || "DeckPanel");
        }

        isEmpty():bool {
            return this.panels.length == 0;
        }

        getSize():number {
            return this.panels.length;
        }

        /*
         * Add new Panel to the deck.
         * @param panel
         * @returns {number} The index for the added Panel.
         */
        addPanel(panel:Panel):number {
            panel.hide();
            panel.setDoOffset(false);
            this.appendChild(panel);
            return this.panels.push(panel) - 1;
        }

        getPanel(index:number) {
            return this.panels[index];
        }

        getLastPanel():Panel {
            return this.getPanel(this.getSize() - 1) || null;
        }

        getPanelShown():Panel {
            return this.panelShown;
        }

        getPanelShownIndex():number {
            return this.getPanelIndex(this.panelShown);
        }

        getPanelIndex(panel:Panel):number {
            var size = this.getSize();
            for (var i = 0; i < size; i++) {
                if (this.panels[i] === panel) {
                    return i;
                }
            }
            return -1;
        }

        /*
         * Removes panel specified by given index. Method canRemovePanel will be called to know if specified panel is allowed to be removed.
         * @returns {Panel} the removed panel. Null if not was not removable.
         */
        removePanelByIndex(index:number, checkCanRemovePanel?:bool = true):Panel {
            var panelToRemove = this.getPanel(index);
            return this.removePanel(panelToRemove, checkCanRemovePanel) ? panelToRemove : null;
        }

        /*
         * Removes given panel. Method canRemovePanel will be called to know if specified panel is allowed to be removed.
         * @returns {number} the index of the removed panel. -1 if it was not removable.
         */
        removePanel(panelToRemove:Panel, checkCanRemovePanel?:bool = true):number {

            var index:number = this.getPanelIndex(panelToRemove);

            if (index < 0) {
                return -1;
            }

            if (checkCanRemovePanel && !this.canRemovePanel(panelToRemove)) {
                return -1;
            }

            panelToRemove.getEl().remove();
            this.panels.splice(index, 1);

            if (this.isEmpty()) {
                this.panelShown = null;
            }
            else if (panelToRemove == this.getPanelShown()) {
                // show either panel that has the same index now or the last panel
                this.showPanel(Math.min(index, this.getSize() - 1));
            }

            return index;
        }

        /*
         * Override this method to decide whether given panel at given index can be removed or not. Default is true.
         */
        canRemovePanel(panel:Panel):bool {
            return true;
        }

        showPanel(index:number) {
            var panelToShow = this.getPanel(index);

            if (panelToShow == null) {
                return;
            }

            if (this.panelShown != null) {
                this.panelShown.hide();
            }

            panelToShow.show();
            this.panelShown = panelToShow;
            this.notifyPanelShown(panelToShow, this.getPanelIndex(panelToShow));
        }

        addListener(listener:DeckPanelListener) {
            this.listeners.push(listener);
        }

        removeListener(listener:DeckPanelListener) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyPanelShown(panel:Panel, panelIndex:number) {
            this.listeners.forEach((listener:DeckPanelListener) => {
                if (listener.onPanelShown) {
                    listener.onPanelShown(panel, panelIndex);
                }
            });
        }
    }
}