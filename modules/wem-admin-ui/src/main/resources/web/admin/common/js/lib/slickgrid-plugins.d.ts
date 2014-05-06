declare module Slick {

    export class RowSelectionModel<T extends Slick.SlickData, E> extends Slick.SelectionModel<T,E> {

        constructor();

        constructor( options:any );

    }

    export class CheckboxSelectColumn<T extends Slick.SlickData> {

        constructor( options:any );

        getColumnDefinition():Slick.Column<T>;
    }
}
