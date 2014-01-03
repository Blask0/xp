module api.dom {

    export class H3El extends Element {

        constructor(idPrefix?:string, className?:string) {
            super("h3", idPrefix, className);
        }

        public setText(value: string) {
            this.getEl().setInnerHtml(value);
        }

    }
}
