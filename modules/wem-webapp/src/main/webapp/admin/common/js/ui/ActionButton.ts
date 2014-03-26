module api.ui {

    export class ActionButton extends api.ui.Button {

        private action: Action;
        private tooltip: Tooltip;

        constructor(action: Action, showTooltip: boolean = true) {
            this.action = action;

            var label: string;
            if (this.action.hasMnemonic()) {
                label = this.action.getMnemonic().underlineMnemonic(this.action.getLabel());
            }
            else {
                label = this.action.getLabel();
            }
            super(label);
            this.addClass("action-button");

            this.setEnabled(this.action.isEnabled());

            if (this.action.getIconClass()) {
                this.addClass(action.getIconClass());
            }

            if (this.action.hasShortcut() && showTooltip) {
                this.tooltip = new Tooltip(this, this.action.getShortcut().getCombination(), 1000);
            }

            this.onClicked((event: MouseEvent) => {
                this.action.execute();
            });

            this.action.onPropertyChanged((action: api.ui.Action) => {
                this.setEnabled(action.isEnabled());
                this.setVisible(action.isVisible());
            });
        }

    }
}
