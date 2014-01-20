module api.ui {

    export class CheckboxInput extends api.dom.InputEl implements api.event.Observable {

        /**
         * Input value before it was changed by last input event.
         */
        private oldValue:boolean = false;

        private listeners:CheckboxInputListener[] = [];

        constructor(className?:string) {
            super(className);

            this.getEl().setAttribute('type', 'checkbox');

            jQuery(this.getHTMLElement()).change(() => {
                var newValue = this.isChecked();
                this.notifyValueChanged(this.oldValue, newValue);
                this.oldValue = newValue;
            });

            this.getEl().addEventListener('input', () => {


            });
        }

        setChecked(newValue:boolean, supressEvent?:boolean):CheckboxInput {
            var oldValue = this.isChecked();

            if (oldValue != newValue) {
                if (newValue) {
                    this.getEl().setAttribute("checked", "checked");
                }
                else {
                    this.getEl().removeAttribute("checked");
                }

                if(!supressEvent) {
                    this.notifyValueChanged(oldValue, newValue);
                }
                // save new value to know which value was before input event.
                this.oldValue = newValue;
            }

            return this;
        }

        isChecked():boolean {
            return this.getEl().hasAttribute("checked");
        }

        setValue(value:string):CheckboxInput {
            throw new Error("CheckboxInput does not support method setValue, use setChecked instead");
        }

        getValue():string {
            throw new Error("CheckboxInput does not support method setValue, use isChecked instead");
        }

        setName(value:string):CheckboxInput {
            super.setName(value);
            return this;
        }

        setPlaceholder(value:string):CheckboxInput {
            this.getEl().setAttribute('placeholder', value);
            return this;
        }

        getPlaceholder():string {
            return this.getEl().getAttribute('placeholder');
        }


        addListener(listener:CheckboxInputListener) {
            this.listeners.push(listener);
        }

        removeListener(listener:CheckboxInputListener) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyValueChanged(oldValue:boolean, newValue:boolean) {
            this.listeners.forEach((listener:CheckboxInputListener) => {
                listener.onValueChanged(oldValue, newValue);
            });
        }
    }
}