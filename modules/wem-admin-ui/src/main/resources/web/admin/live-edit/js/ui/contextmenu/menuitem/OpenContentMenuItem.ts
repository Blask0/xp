module LiveEdit.ui.contextmenu.menuitem {

    export class OpenContentMenuItem extends LiveEdit.ui.contextmenu.menuitem.BaseMenuItem {

        constructor(menu) {
            super({
                text: 'Open in new tab',
                name: 'opencontent',
                handler: (event:Event) => {
                    this.onOpenContent();
                    event.stopPropagation();
                }
            }, menu);

            this.menu = menu;
        }

        private onOpenContent():void {
            /* Fire openContent event in Content Manager */
        }

    }
}