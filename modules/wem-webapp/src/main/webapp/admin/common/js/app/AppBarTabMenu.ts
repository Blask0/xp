module api.app{

    export class AppBarTabMenu extends api.ui.tab.TabMenu {

        private appBarTabMenuButton:AppBarTabMenuButton;

        constructor() {
            super("appbar-tabmenu");
        }

        showMenu() {
            super.showMenu();
            this.updateMenuPosition();
        }

        createTabMenuButton():AppBarTabMenuButton {
            this.appBarTabMenuButton = new AppBarTabMenuButton();
            return this.appBarTabMenuButton;
        }

        addNavigationItem(tab:AppBarTabMenuItem) {
            super.addNavigationItem(tab);

            this.appBarTabMenuButton.setTabCount(this.countVisible());
            this.appBarTabMenuButton.setEditing(tab.isEditing());

            if (this.isShowingMenuItems()) {
                this.updateMenuPosition();
            }
        }

        removeNavigationItem(tab:AppBarTabMenuItem) {
            super.removeNavigationItem(tab);

            this.appBarTabMenuButton.setTabCount(this.countVisible());
            var newSelectedTab = <AppBarTabMenuItem>this.getSelectedNavigationItem();
            if (newSelectedTab) {
                this.appBarTabMenuButton.setEditing(newSelectedTab.isEditing());
            }

            if (this.isShowingMenuItems()) {
                this.updateMenuPosition();
            }
        }

        getNavigationItemById(tabId:AppBarTabId):AppBarTabMenuItem {
            var items:api.ui.tab.TabMenuItem[] = this.getNavigationItems();
            var item;
            for (var i = 0; i < items.length; i++) {
                item = <AppBarTabMenuItem>items[i];
                if (item.getTabId().equals(tabId)) {
                    return item;
                }
            }
            return null;
        }

        selectNavigationItem(tabIndex:number) {
            super.selectNavigationItem(tabIndex);
            var tab = <AppBarTabMenuItem>this.getNavigationItem(tabIndex);
            this.appBarTabMenuButton.setEditing(tab.isEditing());

            this.hideMenu();
        }

        deselectNavigationItem() {
            super.deselectNavigationItem();
            this.appBarTabMenuButton.setEditing(false);
            this.updateMenuPosition();
        }

        /*
         * Aligns tab items list to the center of the tab menu button
         */
        private updateMenuPosition() {
            var containerWidth = this.getEl().getWidth();
            var menuWidth = this.getMenuEl().getEl().getWidth();
            var containerPaddingLeft = this.getEl().getPaddingLeft();

            this.getMenuEl().getEl().setMarginLeft((containerWidth - menuWidth) / 2 - containerPaddingLeft + 'px');
        }
    }
}
