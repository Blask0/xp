module api.content {

    export class ContentVersionViewer extends api.ui.Viewer<ContentVersion> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().
                setSize(api.app.NamesAndIconViewSize.small).
                build();
            this.appendChild(this.namesAndIconView);
        }

        setObject(contentVersion: ContentVersion, row?: number) {
            super.setObject(contentVersion);

            //TODO: use content version image and number instead of row
            this.namesAndIconView.setMainName(row + "        " +
                                              contentVersion.modified.toDateString() + "        " +
                                              contentVersion.displayName).
                setSubName(contentVersion.comment).
                setIconUrl("");
        }

        getPreferredHeight(): number {
            return 50;
        }

    }

}