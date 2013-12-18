module api_dom {

    export class Element {

        private static constructorCounter:number = 0;

        private el:ElementHelper;

        private id:string;

        private parent:Element;

        private children:Element[];

        private rendered:boolean;

        constructor(elementName:string, idPrefix?:string, className?:string, elHelper?:ElementHelper) {
            this.rendered = false;
            if (elHelper == null) {
                this.el = ElementHelper.fromName(elementName);
            } else {
                this.el = elHelper;
            }
            if (idPrefix != null) {
                this.id = idPrefix + '-' + (++Element.constructorCounter);
                this.el.setId(this.id);
            }
            if (className != null) {
                this.el.setClass(className);
            }
            this.children = [];
        }

        static fromHtmlElement(element:HTMLElement):Element {
            return new Element(null, null, null, new ElementHelper(element));
        }

        init() {
            if (!this.isRendered()) {
                this.afterRender();
                this.rendered = true;
            }

            this.children.forEach((child) => {
                child.init();
            })
        }

        reRender() {

            this.children.forEach((child) => {
                child.reRender();
            });

            this.afterRender();
        }

        afterRender() {

        }

        isRendered():boolean {
            return this.rendered;
        }

        className(value:string):Element {
            this.getHTMLElement().className = value;
            return this;
        }

        onElementShown() {
            // To be overridden by inheritors interested in when it's shown
        }

        private broadcastElementShown() {
            this.onElementShown();
            this.children.forEach((child) => {
                child.onElementShown();
            })
        }

        show() {
            // Using jQuery to show, since it seems to contain some smartness
            jQuery(this.el.getHTMLElement()).show();
            this.broadcastElementShown();
        }

        hide() {
            // Using jQuery to hide, since it seems to contain some smartness
            jQuery(this.el.getHTMLElement()).hide();
        }

        setVisible(value:boolean) {
            if (value) {
                this.show();
            }
            else {
                this.hide();
            }
        }

        isVisible() {
            var displayed = this.el.getDisplay() != "none";
            var visible = this.el.getVisibility() != "hidden";
            var sized = this.el.getWidth() != 0 || this.el.getHeight() != 0;
            return displayed && visible && sized;
        }

        empty() {
            this.el.setInnerHtml("");
        }

        setClass(className:string):api_dom.Element {
            this.el.setClass(className);
            return this;
        }

        addClass(className:string):api_dom.Element {
            this.el.addClass(className);
            return this;
        }

        hasClass(className:string):boolean {
            return this.el.hasClass(className);
        }

        removeClass(className:string):api_dom.Element {
            this.el.removeClass(className);
            return this;
        }

        removeAllClasses(exceptions:string = ""):api_dom.Element {
            this.el.getHTMLElement().className = exceptions;
            return this;
        }

        getId():string {
            return this.id;
        }

        getEl():ElementHelper {
            return this.el;
        }

        giveFocus() : boolean  {
            if( !this.isVisible() ) {
                return false;
            }
            if( this.el.isDisabled() ) {
                return false;
            }
            this.el.focuse();
            var gotFocus:boolean = document.activeElement == this.el.getHTMLElement();
            if( !gotFocus )  {
                console.log("Element.giveFocus(): Failed to give focus to Element: class = " + api_util.getClassName(this) + ", id = " + this.getId());
            }
            return gotFocus;
        }

        getHTMLElement():HTMLElement {
            return this.el.getHTMLElement();
        }

        onElementAddedToParent(parent:Element) {
            // To be overridden by inheritors interested in when it's shown
        }

        private broadcastElementAddedToParent(child:Element) {
            child.onElementAddedToParent(this);
        }

        appendChild<T extends api_dom.Element>(child:T) {
            this.el.appendChild(child.getEl().getHTMLElement());
            this.insert(child, this, this.children.length);

            this.broadcastElementAddedToParent(child);
        }

        prependChild(child:api_dom.Element) {
            this.el.getHTMLElement().insertBefore(child.getHTMLElement(), this.el.getHTMLElement().firstChild);
            this.insert(child, this, 0);
        }

        removeChild(child:api_dom.Element) {
            if (this.getHTMLElement().contains(child.getHTMLElement())) {
                this.getHTMLElement().removeChild(child.getHTMLElement());
                this.children = this.children.filter((element) => {
                    return element != child;
                });
            }
        }

        insertAfterEl(existingEl:Element) {
            this.el.insertAfterEl(existingEl);
            var parent = existingEl.getParent();
            var index = parent.getChildren().indexOf(existingEl) + 1;
            this.insert(this, parent, index);
        }

        insertBeforeEl(existingEl:Element) {
            this.el.insertBeforeEl(existingEl);
            var parent = existingEl.getParent();
            var index = parent.getChildren().indexOf(existingEl);
            this.insert(this, parent, index);
        }

        private insert(child:Element, parent:Element, index:number) {
            child.setParent(parent);
            parent.getChildren().splice(index, 0, child);
            if (parent.isRendered()) {
                child.init();
            }
        }

        removeChildren() {
            var htmlEl = this.el.getHTMLElement();
            while (htmlEl.firstChild) {
                htmlEl.removeChild(htmlEl.firstChild);
            }
            this.children = [];
        }

        private setParent(parent:Element) {
            this.parent = parent;
        }

        getParent():Element {
            return this.parent;
        }

        getChildren():Element[] {
            return this.children;
        }

        getLastChild():Element {
            return this.children[this.children.length - 1];
        }

        getFirstChild():Element {
            return this.children[0];
        }

        getCumulativeOffsetTop() {
            var top = 0;
            var element = this.el.getHTMLElement();
            do {
                top += parseInt(element.style.top, 10) || 0;
                element = element.parentElement;
            }
            while (element);

            return top;
        }

        remove() {
            if (this.parent) {
                this.parent.removeChild(this);
            }
        }

        toString():string {
            return jQuery('<div>').append( jQuery(this.getHTMLElement()).clone() ).html();
        }

        onMouseEnter(handler:(e:MouseEvent)=>any) {
            this.mouseEnterLeave(this.getHTMLElement(), 'mouseenter', handler);
        }

        onMouseLeave(handler:(e:MouseEvent)=>any) {
            this.mouseEnterLeave(this.getHTMLElement(), 'mouseleave', handler);
        }

        setBackgroundImgUrl(backgroundImgUrl:string) {
            this.getHTMLElement().style.backgroundImage = "url('" + backgroundImgUrl + "')";
        }

        private mouseEnterLeave(elem:HTMLElement, type:string, handler:(e:MouseEvent)=>any) {
            var mouseEnter = type === 'mouseenter',
                ie = mouseEnter ? 'fromElement' : 'toElement',
                mouseEventHandler = (e:any) => { //Had use any since window.event isn't of type MouseEvent and caused compiler to bug
                    e = e || window.event;
                    var target:HTMLElement = <HTMLElement> (e.target || e.srcElement),
                        related:HTMLElement = <HTMLElement> (e.relatedTarget || e[ie]);
                    if ((elem === target || this.contains(elem, target)) && !this.contains(elem, related)) {
                        handler(e);
                    }
                };
            type = mouseEnter ? 'mouseover' : 'mouseout';

            elem.addEventListener(type, mouseEventHandler);
            return mouseEventHandler;
        }

        private contains(container:HTMLElement, maybe:HTMLElement) {
            return container.contains ? container.contains(maybe) :
                !!(container.compareDocumentPosition(maybe) & 16);
        }

    }
}
