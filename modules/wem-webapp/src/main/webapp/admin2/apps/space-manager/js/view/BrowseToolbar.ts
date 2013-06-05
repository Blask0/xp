module app_ui {

    /**
     * TODO: The upcoming successor of BrowseToolbar, when the Toolbar code is working....
     */
    export class BrowseToolbar extends api_ui_toolbar.Toolbar {

        constructor() {
            super();
            super.addAction(app.SpaceActions.NEW_SPACE);
            super.addAction(app.SpaceActions.EDIT_SPACE);
            super.addAction(app.SpaceActions.OPEN_SPACE);
            super.addAction(app.SpaceActions.DELETE_SPACE);
        }
    }
}
