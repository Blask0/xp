module api.dom {
    export class FormInputEl extends Element {

        constructor(elementName:string, idPrefix?:string, className?:string, elHelper?:ElementHelper) {
            super(elementName, idPrefix, className, elHelper);
        }

        getValue():string {
            return this.getEl().getValue();
        }

        getName():string {
            return this.getEl().getAttribute("name");
        }

        setValue(value:string) {
            this.getEl().setValue(value);
        }
    }
}