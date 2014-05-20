module LiveEdit.ui.contextmenu.menuitem {

    import PageComponentRemoveEvent = api.liveedit.PageComponentRemoveEvent;

    // Uses
    var $ = $liveEdit;

    export class RemoveMenuItem extends LiveEdit.ui.contextmenu.menuitem.BaseMenuItem {

        constructor(menu) {
            super({
                text: 'Remove',
                name: 'remove',
                handler: (event:Event) => {
                    // For demo purposes
                    this.onRemoveComponent();
                    event.stopPropagation();
                }
            }, menu);

            this.menu = menu;
        }

        private onRemoveComponent() {
            this.menu.selectedComponent.getElement().remove();
            new PageComponentRemoveEvent(this.menu.selectedComponent.getComponentPath()).fire();
        }
    }
}