module app_wizard {

    export class RelationshipTypeForm extends api_ui.Form {

        private xmlTextArea:api_ui.CodeArea;

        constructor() {
            super("RelationshipTypeForm");

            var fieldset = new api_ui.Fieldset(this, "Config");
            this.fieldset(fieldset);

            this.xmlTextArea = new api_ui.CodeArea("xml");
            this.xmlTextArea.setSize(api_ui.TextAreaSize.LARGE);
            this.xmlTextArea.setLineNumbers(true);


            fieldset.add(new api_ui.FormItem("XML", this.xmlTextArea));


        }

        getXml():string {
            return this.xmlTextArea.getValue();
        }
    }


}