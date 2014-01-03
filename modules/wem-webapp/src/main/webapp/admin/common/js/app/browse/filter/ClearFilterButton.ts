module api.app.browse.filter {

    export class ClearFilterButton extends api.dom.AEl {

        constructor() {
            super('ClearFilterButton', 'clear-filter-button');
            this.getEl().setInnerHtml('Clear filter');
            this.getHTMLElement().setAttribute('href', 'javascript:;');
            this.hide();
        }
    }
}