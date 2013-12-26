module api_form_formitemset {

    export class FormItemSetView extends api_form.FormItemView {

        private formItemSet: api_form.FormItemSet;

        private dataSets: api_data.DataSet[];

        private occurrenceViewsContainer: api_dom.DivEl;

        private formItemSetOccurrences: FormItemSetOccurrences;

        private bottomButtonRow: api_dom.DivEl;

        private addButton: api_ui.Button;

        private collapseButton: api_ui.Button;

        constructor(context: api_form.FormContext, formItemSet: api_form.FormItemSet, dataSets?: api_data.DataSet[]) {
            super("FormItemSetView", "form-item-set-view", context, formItemSet);

            this.formItemSet = formItemSet;
            this.dataSets = dataSets != null ? dataSets : [];

            this.occurrenceViewsContainer = new api_dom.DivEl(null, "occurrence-views-container");
            this.appendChild(this.occurrenceViewsContainer);

            this.formItemSetOccurrences =
            new FormItemSetOccurrences(this.getContext(), this.occurrenceViewsContainer, formItemSet, dataSets);
            this.formItemSetOccurrences.layout();
            this.formItemSetOccurrences.addListener(<api_form.FormItemOccurrencesListener>{
                onOccurrenceAdded: (occurrenceAdded: api_form.FormItemOccurrence<any>) => {
                    this.refresh();
                },
                onOccurrenceRemoved: (occurrenceRemoved: api_form.FormItemOccurrence<any>) => {
                    this.refresh();
                }
            });

            this.bottomButtonRow = new api_dom.DivEl(null, "bottom-button-row");
            this.appendChild(this.bottomButtonRow);

            this.addButton = new api_ui.Button("Add " + this.formItemSet.getLabel());
            this.addButton.setClass("add-button");
            this.addButton.setClickListener(() => {
                this.formItemSetOccurrences.createAndAddOccurrence();
                if (this.formItemSetOccurrences.isCollapsed()) {
                    this.collapseButton.getHTMLElement().click();
                }

            });
            this.collapseButton = new api_ui.Button("Collapse");
            this.collapseButton.setClass("collapse-button");
            this.collapseButton.setClickListener(() => {
                if (this.formItemSetOccurrences.isCollapsed()) {
                    this.collapseButton.setText("Collapse");
                    this.formItemSetOccurrences.toggleOccurences(true);
                } else {
                    this.collapseButton.setText("Expand");
                    this.formItemSetOccurrences.toggleOccurences(false);
                }

            });

            this.bottomButtonRow.appendChild(this.addButton);
            this.bottomButtonRow.appendChild(this.collapseButton);
            this.refresh();
        }

        refresh() {

            this.addButton.setVisible(!this.formItemSetOccurrences.maximumOccurrencesReached());
        }

        public getFormItemSetOccurrenceView(index: number): FormItemSetOccurrenceView {
            return this.formItemSetOccurrences.getFormItemSetOccurrenceView(index);
        }

        getData(): api_data.Data[] {
            return this.getDataSets();
        }

        getDataSets(): api_data.DataSet[] {

            return this.formItemSetOccurrences.getDataSets();
        }

        getAttachments(): api_content_attachment.Attachment[] {
            return this.formItemSetOccurrences.getAttachments();
        }

        hasValidOccurrences(): boolean {

            return this.getData().length >= this.formItemSet.getOccurrences().getMaximum();
        }

        validate(validationRecorder: api_form.ValidationRecorder) {

            // TODO:
        }

        giveFocus(): boolean {

            var focusGiven = false;
            if (this.formItemSetOccurrences.getOccurrenceViews().length > 0) {
                var views:api_form.FormItemOccurrenceView[] = this.formItemSetOccurrences.getOccurrenceViews();
                for (var i = 0; i < views.length; i++) {
                    if (views[i].giveFocus()) {
                        focusGiven = true;
                        break;
                    }
                }
            }
            return focusGiven;
        }
    }
}