module api_ui {

    export class Panel extends api_dom.DivEl {

        private doOffset:boolean;

        constructor(idPrefix?:string, className?:string) {
            super(idPrefix, "panel" + (className ? " " + className : ""));
            this.doOffset = true;
        }

        showCallback() {
            if(this.doOffset) {
                this.calculateOffset();
            }
        }

        afterRender() {
            if(this.doOffset) {
                this.calculateOffset();
            }
        }

        setDoOffset(value:boolean) {
            this.doOffset = value;
        }

        setScrollY() {
            this.addClass("scroll-y");
        }

        private calculateOffset() {
            // calculates bottom of previous element in dom and set panel top to this value.
            var previous = this.getEl().getPrevious();
            var top = previous ? (previous.getOffsetTop() + previous.getHeight()) : 0;

            this.getEl().setTopPx(top);
        }
    }

}