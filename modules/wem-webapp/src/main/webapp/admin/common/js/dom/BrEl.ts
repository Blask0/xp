module api.dom {

    export class BrEl extends Element {

        constructor() {
            super(new ElementProperties().setTagName("br"));
        }
    }
}
