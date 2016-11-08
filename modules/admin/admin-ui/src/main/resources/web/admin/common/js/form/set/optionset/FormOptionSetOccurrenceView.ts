module api.form {

    import PropertySet = api.data.PropertySet;
    import PropertyArray = api.data.PropertyArray;
    import PropertyPath = api.data.PropertyPath;
    import ValueTypes = api.data.ValueTypes;
    import Value = api.data.Value;

    export interface FormOptionSetOccurrenceViewConfig {
        context: FormContext;

        formSetOccurrence: FormSetOccurrence<FormOptionSetOccurrenceView>;

        formOptionSet: FormOptionSet;

        parent: FormItemOccurrenceView;

        dataSet: PropertySet
    }

    export class FormOptionSetOccurrenceView extends FormSetOccurrenceView {

        private formOptionSet: FormOptionSet;

        private context: FormContext;

        constructor(config: FormOptionSetOccurrenceViewConfig) {
            super("form-option-set-occurrence-view", config.formSetOccurrence);
            this.occurrenceContainerClassName = "form-option-set-occurrences-container";
            this.formItemOccurrence = config.formSetOccurrence;
            this.formOptionSet = config.formOptionSet;
            this.propertySet = config.dataSet;
            this.ensureSelectionArrayExists(this.propertySet);

            this.formItemLayer = new FormItemLayer(config.context);
            this.context = config.context;
        }

        protected subscribeOnItemEvents() {
            this.formItemViews.forEach((formItemView: FormItemView) => {
                formItemView.onValidityChanged((event: RecordingValidityChangedEvent) => {

                    if (!this.currentValidationState) {
                        return; // currentValidationState is initialized on validate() call which may not be triggered in some cases
                    }

                    if (this.isNew()) {
                        this.currentValidationState = new ValidationRecording();
                        return;
                    }

                    var previousValidState = this.currentValidationState.isValid();
                    if (event.isValid()) {
                        this.currentValidationState.removeByPath(event.getOrigin(), false, event.isIncludeChildren());
                    } else {
                        this.currentValidationState.flatten(event.getRecording());
                    }

                    if (previousValidState != this.currentValidationState.isValid()) {
                        this.notifyValidityChanged(new RecordingValidityChangedEvent(this.currentValidationState,
                            this.resolveValidationRecordingPath()).setIncludeChildren(true));
                    }
                });

                (<FormOptionSetOptionView> formItemView).onSelectionChanged(() => {
                    if (!this.currentValidationState) {
                        return; // currentValidationState is initialized on validate() call which may not be triggered in some cases
                    }

                    if (this.isNew()) {
                        this.currentValidationState = new ValidationRecording();
                        return;
                    }

                    var previousValidationValid = this.currentValidationState.isValid(),
                        multiselectionState = this.validateMultiselection();

                    if (multiselectionState.isValid()) {
                        if (this.formOptionSet.isRadioSelection()) { // for radio - we clean all validation, as even selected item should not be validated
                            this.currentValidationState.removeByPath(
                                new ValidationRecordingPath(this.getDataPath(), null), true, true);

                        } else {
                            this.currentValidationState.removeByPath(
                                new ValidationRecordingPath(this.getDataPath(), formItemView.getFormItem().getName()), true, true);
                        }
                    } else {
                        this.currentValidationState.flatten(this.currentValidationState);
                    }

                    if (this.currentValidationState.isValid() != previousValidationValid) {
                        this.notifyValidityChanged(new RecordingValidityChangedEvent(this.currentValidationState,
                            this.resolveValidationRecordingPath()).setIncludeChildren(true));
                    }
                })
            });
        }

        private isNew(): boolean {
            if (api.ObjectHelper.iFrameSafeInstanceOf(this.context, api.content.form.ContentFormContext)) {
                var contentFormContext = <api.content.form.ContentFormContext> this.context;
                if (contentFormContext.getFormState() != null && contentFormContext.getFormState().isNew()) {
                    return true;
                }
            }
            return false;
        }

        private makeMultiselectionNote(): string {
            var multiselection = this.formOptionSet.getMultiselection();
            if (multiselection.getMinimum() == 1 && multiselection.getMaximum() == 1) {
                return null;
            }

            if (multiselection.getMinimum() == 0 && multiselection.getMaximum() == 0) {
                return "(any)"
            }
            if (multiselection.getMinimum() > 0 && multiselection.getMaximum() == 0) {
                return "(at least " + multiselection.getMinimum() + ")";
            }
            if (multiselection.getMinimum() > 1 && multiselection.getMinimum() == multiselection.getMaximum()) {
                return "(pick " + multiselection.getMinimum() + ")";
            }
            if (multiselection.getMinimum() == 0 && multiselection.getMaximum() > 1) {
                return "(up to " + multiselection.getMaximum() + ")";
            }
            if (multiselection.getMinimum() > 0 && multiselection.getMaximum() > multiselection.getMinimum()) {
                return "(" + multiselection.getMinimum() + " to " + multiselection.getMaximum() + ")";
            }
            if (multiselection.getMinimum() == 0 && multiselection.getMaximum() == 1) {
                return "(0 or 1)";
            }
            return null;
        }

        protected ensureSelectionArrayExists(propertyArraySet: PropertySet) {
            var selectionPropertyArray = propertyArraySet.getPropertyArray("_selected");
            if (!selectionPropertyArray) {
                selectionPropertyArray =
                    PropertyArray.create().setType(ValueTypes.STRING).setName("_selected").setParent(
                        propertyArraySet).build();
                propertyArraySet.addPropertyArray(selectionPropertyArray);
                this.addDefaultSelectionToSelectionArray(selectionPropertyArray);
            }
        }

        private addDefaultSelectionToSelectionArray(selectionPropertyArray: PropertyArray) {
            this.formOptionSet.getOptions().forEach((option: FormOptionSetOption) => {
                if (option.isDefaultOption() && selectionPropertyArray.getSize() < this.formOptionSet.getMultiselection().getMaximum()) {
                    selectionPropertyArray.add(new Value(option.getName(), new api.data.ValueTypeString()))
                }
            });
        }

        protected extraValidation(validationRecording: ValidationRecording) {
            validationRecording.flatten(this.validateMultiselection());
        }

        private validateMultiselection(): ValidationRecording {
            var multiselectionRecording = new ValidationRecording(),
                validationRecordingPath = this.resolveValidationRecordingPath(),
                selectionPropertyArray = this.propertySet.getPropertyArray("_selected");

            if (selectionPropertyArray.getSize() < this.formOptionSet.getMultiselection().getMinimum()) {
                multiselectionRecording.breaksMinimumOccurrences(validationRecordingPath);
            }

            if (this.formOptionSet.getMultiselection().maximumBreached(selectionPropertyArray.getSize())) {
                multiselectionRecording.breaksMaximumOccurrences(validationRecordingPath);
            }

            if (this.currentValidationState) {
                if (selectionPropertyArray.getSize() < this.formOptionSet.getMultiselection().getMinimum()) {
                    this.currentValidationState.breaksMinimumOccurrences(validationRecordingPath);
                } else {
                    this.currentValidationState.removeUnreachedMinimumOccurrencesByPath(validationRecordingPath, false);
                }

                if (this.formOptionSet.getMultiselection().maximumBreached(selectionPropertyArray.getSize())) {
                    this.currentValidationState.breaksMaximumOccurrences(validationRecordingPath);
                } else {
                    this.currentValidationState.removeBreachedMaximumOccurrencesByPath(validationRecordingPath, false);
                }
            }

            return multiselectionRecording;
        }

        protected getFormSet(): FormSet {
            return this.formOptionSet;
        }

        protected getFormItems(): FormItem[] {
            return this.formOptionSet.getFormItems();
        }
    }
}