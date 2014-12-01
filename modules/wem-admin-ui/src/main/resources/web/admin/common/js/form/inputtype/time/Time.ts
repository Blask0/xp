module api.content.form.inputtype.time {

    import support = api.form.inputtype.support;
    import Property = api.data2.Property;
    import Value = api.data2.Value;
    import ValueType = api.data2.ValueType;
    import ValueTypes = api.data2.ValueTypes;

    export class Time extends support.BaseInputTypeNotManagingAdd<any,api.util.LocalTime> {

        constructor(config: api.form.inputtype.InputTypeViewContext<any>) {
            super(config);
        }

        getValueType(): ValueType {
            return ValueTypes.LOCAL_TIME;
        }

        newInitialValue(): Value {
            return ValueTypes.LOCAL_TIME.newNullValue();
        }

        createInputOccurrenceElement(index: number, property: Property): api.dom.Element {
            var localTimeEl = new api.ui.time.LocalTime();

            var timeValue: api.util.LocalTime = property.getLocalTime();
            if (timeValue) {
                localTimeEl.setTime(timeValue);
            }

            localTimeEl.onTimeChanged((hours: number, minutes: number) => {
                var utcTime = api.util.DateHelper.parseUTCTime(hours + ":" + minutes);
                var changedValue: Value = ValueTypes.LOCAL_TIME.newValue(utcTime);
                property.setValue(changedValue);
            });

            return localTimeEl;
        }

        availableSizeChanged() {
        }

        valueBreaksRequiredContract(value: Value): boolean {
            return value.isNull() || !value.getType().equals(ValueTypes.LOCAL_TIME);
        }

    }
    api.form.inputtype.InputTypeManager.register(new api.Class("Time", Time));

}