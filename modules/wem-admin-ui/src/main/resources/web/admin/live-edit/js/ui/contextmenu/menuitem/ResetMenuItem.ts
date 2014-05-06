module LiveEdit.ui.contextmenu.menuitem {

    // Uses
    var $ = $liveEdit;

    export class ResetMenuItem extends LiveEdit.ui.contextmenu.menuitem.BaseMenuItem {

        constructor(menu) {
            super({
                text: 'Reset',
                name: 'reset',
                handler: (event:Event) => {
                    this.onReset();
                    event.stopPropagation();
                }
            }, menu);

            this.menu = menu;
        }

        private onReset() {
            /**/
        }

    }
}