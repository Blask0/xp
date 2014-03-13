module api.ui.menu {

    export class MenuItem extends api.dom.LiEl {

        private action:api.ui.Action;

        constructor(action:api.ui.Action) {
            super("menu-item");
            this.action = action;
            this.getEl().setInnerHtml(this.action.getLabel());
            this.onClicked((event: MouseEvent) => {
                if (action.isEnabled()) {
                    this.action.execute();
                }
            });
            this.setEnable(action.isEnabled());

            action.addPropertyChangeListener((action:api.ui.Action) => {
                this.setEnable(action.isEnabled());
            });
        }

        setEnable(value:boolean) {
            var el = this.getEl();
            el.setDisabled(!value);
            if (value) {
                el.removeClass("disabled");
            } else {
                el.addClass("disabled");
            }
        }
    }

}