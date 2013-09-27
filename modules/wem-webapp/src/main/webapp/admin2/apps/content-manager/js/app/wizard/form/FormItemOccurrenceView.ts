module app_wizard_form {

    export class FormItemOccurrenceView extends api_dom.DivEl {

        private formItemOccurrence:app_wizard_form.FormItemOccurrence;

        private listeners:FormItemOccurrenceViewListener[] = [];

        constructor(idPrefix:string, className, formItemOccurrence:FormItemOccurrence) {
            super(idPrefix, className);
            this.formItemOccurrence = formItemOccurrence;
        }

        addListener(listener:FormItemOccurrenceViewListener) {
            this.listeners.push(listener);
        }

        removeListener(listener:FormItemOccurrenceViewListener) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr != listener;
            });
        }

        notifyRemoveButtonClicked() {
            this.listeners.forEach((listener:FormItemOccurrenceViewListener) => {
                listener.onRemoveButtonClicked(this, this.formItemOccurrence.getIndex());
            });
        }

        getIndex():number {
            return this.formItemOccurrence.getIndex();
        }

        refresh() {
            throw new Error("Must be implemented by inheritor");
        }
    }
}