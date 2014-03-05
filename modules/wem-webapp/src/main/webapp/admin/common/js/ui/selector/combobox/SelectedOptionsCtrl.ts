module api.ui.selector.combobox {

    export class SelectedOptionsCtrl<T>{

        private selectedOptionsView:SelectedOptionsView<T>;

        private maximumOccurrences:number;

        private selectedOptions:SelectedOptions<T> = new SelectedOptions<T>();

        private selectedOptionRemovedListeners:{(removed:SelectedOption<T>): void;}[] = [];

        constructor(selectedOptionsView:SelectedOptionsView<T>, maximumOccurrences:number) {
            this.selectedOptionsView = selectedOptionsView;
            this.selectedOptionsView.setSelectedOptions(this.selectedOptions);
            this.maximumOccurrences = maximumOccurrences;
        }

        count():number {
            return this.selectedOptions.count();
        }

        maximumOccurrencesReached():boolean {
            if (this.maximumOccurrences == 0) {
                return false;
            }
            return this.count() >= this.maximumOccurrences;
        }

        getOptions():api.ui.selector.Option<T>[] {
            return this.selectedOptions.getOptions();
        }

        addOption(option:api.ui.selector.Option<T>):boolean {

            if (!this.canAdd(option)) {
                return false;
            }

            var selectedOption:SelectedOption<T> = this.selectedOptionsView.createSelectedOption(option, this.selectedOptions.count());
            var selectedOptionView = selectedOption.getOptionView();

            selectedOptionView.addSelectedOptionToBeRemovedListener((toBeRemoved:SelectedOptionView<T>) => {
                var selectedOption = this.selectedOptions.getByView(toBeRemoved);
                this.removeSelectedOption(selectedOption);
            });

            this.selectedOptionsView.addOptionView(selectedOption);
            this.selectedOptions.add(selectedOption);
            return true;
        }

        private canAdd(option:api.ui.selector.Option<T>):boolean {
            if (this.maximumOccurrencesReached()) {
                return false;
            }
            return this.selectedOptions.getByOption(option) == null;
        }

        //TODO: Theese block contextwindows stuff. Probably because the OptionData.value is faked
        removeOption(optionToRemove:api.ui.selector.Option<T>, silent:boolean = false) {
            api.util.assertNotNull(optionToRemove, "optionToRemove cannot be null");

            var selectedOption = this.selectedOptions.getByOption( optionToRemove );
            api.util.assertNotNull(selectedOption, "Did not find any selected option to remove from option: " + optionToRemove.value);

            this.removeSelectedOption(selectedOption, silent);
        }

        private removeSelectedOption(optionToBeRemoved:SelectedOption<T>, silent:boolean = false) {
            api.util.assertNotNull(optionToBeRemoved, "optionToBeRemoved cannot be null");

            this.selectedOptions.remove( optionToBeRemoved );
            for( var i:number = optionToBeRemoved.getIndex(); i < this.count(); i++ ) {
                this.selectedOptions.getSelectedOption(i).setIndex(i);
            }

            this.selectedOptionsView.removeOptionView(optionToBeRemoved);


            if( !silent ) {
                this.notifySelectedOptionRemoved(optionToBeRemoved);
            }
        }

        private notifySelectedOptionRemoved(removed:SelectedOption<T>) {
            this.selectedOptionRemovedListeners.forEach( (listener) => {
                listener(removed);
            });
        }

        addSelectedOptionRemovedListener(listener:{(removed:SelectedOption<T>): void;}) {
            this.selectedOptionRemovedListeners.push(listener);
        }

        removeSelectedOptionRemovedListener(listener:{(removed:SelectedOption<T>): void;}) {
            this.selectedOptionRemovedListeners = this.selectedOptionRemovedListeners.filter(function (curr) {
                return curr != listener;
            });
        }
    }
}

