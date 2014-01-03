module api.ui.combobox {

    export class SelectedOptions<T>{

        private list:SelectedOption<T>[] = [];

        count():number {
            return this.list.length;
        }

        getSelectedOption(index:number):SelectedOption<T> {
            return this.list[index];
        }

        getOptions():Option<T>[] {
            var options:Option<T>[] = [];
            this.list.forEach( (selectedOption:SelectedOption<T>) => {
                options.push(selectedOption.getOption());
            } );
            return options;
        }

        getOptionViews():SelectedOptionView<T>[] {
            var options:SelectedOptionView<T>[] = [];
            this.list.forEach( (selectedOption:SelectedOption<T>) => {
                options.push(selectedOption.getOptionView());
            } );
            return options;
        }

        add(selectedOption:SelectedOption<T>) {
            this.list.push(selectedOption);
        }

        getByView(view:SelectedOptionView<T>) {

            for(var i = 0; i < this.list.length; i++) {
                if( this.list[i].getOptionView() == view ) {
                    return this.list[i];
                }
            }
            return null;
        }

        getByOption(option:Option<T>):SelectedOption<T> {

            for(var i = 0; i < this.list.length; i++) {
                if( this.list[i].getOption() == option ) {
                    return this.list[i];
                }
            }
            return null;
        }

        remove(selectedOption:SelectedOption<T>) {

            this.list = this.list.filter( (option:SelectedOption<T>) => {
                return option.getOption().value != selectedOption.getOption().value;
            });
        }
    }
}

