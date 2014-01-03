module api.ui{

    export class ActionButton extends api.dom.ButtonEl {

        private action:Action;
        private tooltip:Tooltip;

        constructor(idPrefix:string, action:Action, showTooltip:boolean = true) {
            super(idPrefix);

            this.action = action;

            if (this.action.getIconClass()) {
                this.getEl().addClass(action.getIconClass());
            }

            this.setEnabled(this.action.isEnabled());

            if (this.action.hasMnemonic()) {
                var htmlNodes:Node[] = this.action.getMnemonic().underlineMnemonic(this.action.getLabel());
                this.getEl().appendChildren(htmlNodes);
            }
            else {
                this.getEl().appendChild(document.createTextNode(this.action.getLabel()));
            }

            if (this.action.hasShortcut() && showTooltip) {
                this.tooltip = new Tooltip(this, this.action.getShortcut().getCombination(), 1000);
            }

            this.getEl().addEventListener("click", () => {
                this.action.execute();
            });

            this.action.addPropertyChangeListener((action:api.ui.Action) => {
                this.setEnabled(action.isEnabled());
            });
        }

        setEnabled(value:boolean) {
            this.getEl().setDisabled(!value);
        }

        isEnabled() {
            return !this.getEl().isDisabled();
        }

    }
}
