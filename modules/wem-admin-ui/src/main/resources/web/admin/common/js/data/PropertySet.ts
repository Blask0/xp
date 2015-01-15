module api.data {

    export class PropertySet implements api.Equitable {

        public debug: boolean = true;

        private tree: PropertyTree = null;

        private property: Property = null;

        private propertyArrayByName: {[s:string] : PropertyArray;} = {};

        private _ifNotNull: boolean = false;

        private changedListeners: {(event: PropertyEvent):void}[] = [];

        private propertyAddedListeners: {(event: PropertyAddedEvent):void}[] = [];

        private propertyRemovedListeners: {(event: PropertyRemovedEvent):void}[] = [];

        private propertyIndexChangedListeners: {(event: PropertyIndexChangedEvent):void}[] = [];

        private propertyValueChangedListeners: {(event: PropertyValueChangedEvent):void}[] = [];

        constructor(tree?: PropertyTree) {
            this.tree = tree;
        }

        setContainerProperty(value: Property) {
            this.property = value;
        }

        isDetached(): boolean {
            return !this.tree;
        }

        getTree(): PropertyTree {
            return this.tree;
        }

        attachToTree(tree: PropertyTree) {
            this.tree = tree;

            this.forEach((property: Property) => {
                property.setId(this.tree.getNextId());
                this.tree.registerProperty(property);
                if (property.hasNonNullValue() && property.getType().equals(ValueTypes.DATA)) {
                    property.getSet().attachToTree(tree);
                }
            });
        }

        /**
         * If invoked, then the next added or set property with a null will be ignored.
         * The second add or set after this method is called will not be affected.
         */
        public ifNotNull(): PropertySet {
            this._ifNotNull = true;
            return this;
        }

        addPropertyArray(array: PropertyArray) {
            api.util.assertState(this.tree === array.getTree(),
                "Added PropertyArray must be attached to the same PropertyTree as this PropertySet");
            api.util.assert(this == array.getParent(), "propertyArray must have this PropertySet as parent");
            this.propertyArrayByName[array.getName()] = array;

            this.registerPropertyArrayListeners(array);
        }

        addProperty(name: string, value: Value): Property {

            if (this._ifNotNull && value.isNull()) {
                this._ifNotNull = false;
                return null;
            }

            var array = this.getOrCreatePropertyArray(name, value.getType());
            var property = array.add(value);
            return property;
        }

        setPropertyByPath(path: any, value: Value): Property {
            if (api.ObjectHelper.iFrameSafeInstanceOf(path, PropertyPath)) {
                return this.doSetProperty(<PropertyPath>path, value)
            }
            else {
                return this.doSetProperty(PropertyPath.fromString(path.toString()), value);
            }
        }

        private doSetProperty(path: PropertyPath, value: Value): Property {
            var firstPathElement = path.getFirstElement();
            if (path.elementCount() > 1) {
                var propertySet = this.getOrCreateSet(firstPathElement.getName(), firstPathElement.getIndex());
                return propertySet.setPropertyByPath(path.removeFirstPathElement(), value);
            }
            else {
                return this.setProperty(firstPathElement.getName(), firstPathElement.getIndex(), value);
            }
        }

        private getOrCreateSet(name: string, index: number): PropertySet {
            var existingProperty = this.getProperty(name, index);
            if (!existingProperty) {
                var newSet = this.tree ? new PropertySet(this.tree) : new PropertySet();
                this.setProperty(name, index, new Value(newSet, ValueTypes.DATA));
                return newSet;
            }
            else {
                return existingProperty.getSet();
            }
        }

        setProperty(name: string, index: number, value: Value): Property {

            var array = this.getOrCreatePropertyArray(name, value.getType());
            return array.set(index, value);
        }

        private getOrCreatePropertyArray(name: string, type: ValueType): PropertyArray {

            var array = this.propertyArrayByName[name];
            if (!array) {
                array = PropertyArray.create().
                    setParent(this).
                    setName(name).
                    setType(type).
                    build();
                this.propertyArrayByName[name] = array;
                this.registerPropertyArrayListeners(array);
            }
            return array;
        }

        removeProperty(name: string, index: number) {
            var array: PropertyArray = this.propertyArrayByName[name];
            if (array) {
                array.remove(index);
            }
        }


        getSize(): number {
            var size = 0;
            api.ObjectHelper.objectPropertyIterator(this.propertyArrayByName, (name: string, propertyArray: PropertyArray) => {
                size += propertyArray.getSize();
            });

            return size;
        }

        countProperties(name: string): number {
            var array = this.propertyArrayByName[name];
            if (!array) {
                return 0;
            }
            return array.getSize();
        }

        getPropertyPath(): PropertyPath {
            return !this.property ? PropertyPath.ROOT : this.property.getPath();
        }

        /**
         * If no arguments are given then this PropertySet's Property is returned.
         * If name and index are given then property with that name and index is returned.
         * @param identifier
         * @param index
         * @returns {*}
         */
        getProperty(identifier?: any, index?: number): Property {

            if (identifier == undefined && index == undefined) {
                return this.property;
            }
            else if (index != undefined) {
                Property.checkName(identifier);
                var array = this.propertyArrayByName[identifier];
                if (!array) {
                    return null;
                }
                return array.get(index);
            }
            else {
                return this.getPropertyByPath(identifier);
            }
        }

        private getPropertyByPath(path: any): Property {

            if (api.ObjectHelper.iFrameSafeInstanceOf(path, PropertyPath)) {
                return this.doGetPropertyByPath(<PropertyPath>path);
            }
            else {
                return this.doGetPropertyByPath(PropertyPath.fromString(path.toString()));
            }
        }

        private doGetPropertyByPath(path: PropertyPath): Property {

            var firstElement = path.getFirstElement();
            if (path.elementCount() > 1) {
                var property = this.getProperty(firstElement.getName(), firstElement.getIndex());
                if (!property) {
                    return null;
                }
                var propertySet = property.getSet();
                return propertySet.getPropertyByPath(path.removeFirstPathElement());
            }
            else {
                return this.getProperty(firstElement.getName(), firstElement.getIndex());
            }
        }

        getPropertyArray(name: string): PropertyArray {
            return this.propertyArrayByName[name];
        }

        /**
         * Calls the given callback for each property in the set.
         */
        forEach(callback: (property: Property, index?: number) => void) {
            api.ObjectHelper.objectPropertyIterator(this.propertyArrayByName, (name: string, propertyArray: PropertyArray) => {
                propertyArray.forEach((property: Property, index: number) => {
                    callback(property, index);
                });
            });
        }

        /**
         * Calls the given callback for each property with the given name.
         */
        forEachProperty(propertyName: string, callback: (property: Property, index?: number) => void) {
            var array = this.getPropertyArray(propertyName);
            if (array) {
                array.forEach(callback);
            }
        }

        public isNotNull(identifier: any, index?: number): boolean {
            var property = this.getProperty(identifier, index);
            if (property == null) {
                return false;
            }

            return !property.hasNullValue();
        }

        public isNull(identifier: any, index?: number): boolean {
            return !this.isNotNull(identifier, index);
        }

        public equals(o: any): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, PropertySet)) {
                return false;
            }

            var other = <PropertySet>o;

            if (!api.ObjectHelper.mapEquals(this.propertyArrayByName, other.propertyArrayByName)) {
                return false;
            }

            return true;
        }

        toTree(idProvider?: PropertyIdProvider): PropertyTree {
            if (!this.tree && !idProvider) {
                throw new Error("The PropertySet must be attached to a PropertyTree or a idProvider must be given before when this method is invoked");
            }
            if (idProvider) {
                return new PropertyTree(idProvider, this);
            }
            else {
                return new PropertyTree(this.tree.getIdProvider(), this);
            }
        }

        copy(destinationTree: PropertyTree, generateNewPropertyIds: boolean = false): PropertySet {

            var copy = new PropertySet(destinationTree);

            api.ObjectHelper.objectPropertyIterator(this.propertyArrayByName, (name: string, sourcePropertyArray: PropertyArray) => {
                var propertyArrayCopy = sourcePropertyArray.copy(copy, generateNewPropertyIds);
                copy.addPropertyArray(propertyArrayCopy);
            });

            return copy;
        }

        toJson(): PropertyArrayJson[] {
            var jsonArray: PropertyArrayJson[] = [];

            api.ObjectHelper.objectPropertyIterator(this.propertyArrayByName, (name: string, propertyArray: PropertyArray) => {
                jsonArray.push(propertyArray.toJson());
            });

            return jsonArray;
        }

        private registerPropertyArrayListeners(array: PropertyArray) {
            if (this.debug) {
                console.debug("PropertySet[" + this.getPropertyPath().toString() + "].registerPropertyArrayListeners: " + array.getName())
            }

            if (this.forwardPropertyAddedEvent.bind) {
                array.onPropertyAdded(this.forwardPropertyAddedEvent.bind(this));
                array.onPropertyRemoved(this.forwardPropertyRemovedEvent.bind(this));
                array.onPropertyIndexChanged(this.forwardPropertyIndexChangedEvent.bind(this));
                array.onPropertyValueChanged(this.forwardPropertyValueChangedEvent.bind(this));
            }
            else {
                // PhantomJS does not support bind
                array.onPropertyAdded((event) => {
                    this.forwardPropertyAddedEvent(event);
                });
                array.onPropertyRemoved((event) => {
                    this.forwardPropertyRemovedEvent(event);
                });
                array.onPropertyIndexChanged((event) => {
                    this.forwardPropertyIndexChangedEvent(event);
                });
                array.onPropertyValueChanged((event) => {
                    this.forwardPropertyValueChangedEvent(event);
                });
            }
        }

        // Currently not used, because we do not remove arrays
        private unregisterPropertyArrayListeners(array: PropertyArray) {
            array.unPropertyAdded(this.forwardPropertyAddedEvent);
            array.unPropertyRemoved(this.forwardPropertyRemovedEvent);
            array.unPropertyIndexChanged(this.forwardPropertyIndexChangedEvent);
            array.unPropertyValueChanged(this.forwardPropertyValueChangedEvent);
        }

        onChanged(listener: {(event: PropertyEvent): void;}) {
            this.changedListeners.push(listener);
        }

        unChanged(listener: {(event: PropertyEvent): void;}) {
            this.changedListeners = this.changedListeners.filter((curr) => (curr != listener));
        }

        private notifyChangedListeners(event: PropertyEvent) {
            if (this.debug) {
                console.debug("PropertySet[" + this.getPropertyPath().toString() + "].notifyChangedListeners: " +
                              event.getPath().toString());
            }
            this.changedListeners.forEach((listener) => listener(event));
        }

        onPropertyAdded(listener: {(event: PropertyAddedEvent): void;}) {
            this.propertyAddedListeners.push(listener);
        }

        unPropertyAdded(listener: {(event: PropertyAddedEvent): void;}) {
            this.propertyAddedListeners = this.propertyAddedListeners.filter((curr) => (curr != listener));
        }

        private forwardPropertyAddedEvent(event: PropertyAddedEvent) {
            this.propertyAddedListeners.forEach((listener) => listener(event));
            if (this.debug) {
                console.debug("PropertySet[" + this.getPropertyPath().toString() + "].forwardPropertyAddedEvent: " +
                              event.getPath().toString());
            }
            this.notifyChangedListeners(event);
        }

        onPropertyRemoved(listener: {(event: PropertyRemovedEvent): void;}) {
            this.propertyRemovedListeners.push(listener);
        }

        unPropertyRemoved(listener: {(event: PropertyRemovedEvent): void;}) {
            this.propertyRemovedListeners = this.propertyRemovedListeners.filter((curr) => (curr != listener));
        }

        private forwardPropertyRemovedEvent(event: PropertyRemovedEvent) {
            if (this.debug) {
                console.debug("PropertySet[" + this.getPropertyPath().toString() + "].forwardPropertyRemovedEvent: " +
                              event.getPath().toString());
            }
            this.propertyRemovedListeners.forEach((listener) => listener(event));
            this.notifyChangedListeners(event);
        }

        onPropertyIndexChanged(listener: {(event: PropertyIndexChangedEvent): void;}) {
            this.propertyIndexChangedListeners.push(listener);
        }

        unPropertyIndexChanged(listener: {(event: PropertyIndexChangedEvent): void;}) {
            this.propertyIndexChangedListeners = this.propertyIndexChangedListeners.filter((curr) => (curr != listener));
        }

        private forwardPropertyIndexChangedEvent(event: PropertyIndexChangedEvent) {
            if (this.debug) {
                console.debug("PropertySet[" + this.getPropertyPath().toString() + "].forwardPropertyIndexChangedEvent: " +
                              event.getPath().toString());
            }
            this.propertyIndexChangedListeners.forEach((listener) => listener(event));
            this.notifyChangedListeners(event);
        }

        onPropertyValueChanged(listener: {(event: PropertyValueChangedEvent): void;}) {
            this.propertyValueChangedListeners.push(listener);
        }

        unPropertyValueChanged(listener: {(event: PropertyValueChangedEvent): void;}) {
            this.propertyValueChangedListeners = this.propertyValueChangedListeners.filter((curr) => (curr != listener));
        }

        private forwardPropertyValueChangedEvent(event: PropertyValueChangedEvent) {
            if (this.debug) {
                console.debug("PropertySet[" + this.getPropertyPath().toString() + "].forwardPropertyValueChangedEvent: " +
                              event.getPath().toString());
            }
            this.propertyValueChangedListeners.forEach((listener) => listener(event));
            this.notifyChangedListeners(event);
        }

        // PropertySet methods

        newSet(): PropertySet {
            if (!this.tree) {
                throw new Error("The PropertySet must be attached to a PropertyTree before this method can be invoked. Use PropertySet constructor with no arguments instead.");
            }
            return this.tree.newSet();
        }

        addSet(name: string, value?: PropertySet): PropertySet {
            if (!value) {
                if (!this.tree) {
                    throw new Error("The PropertySet must be attached to a PropertyTree before this method can be invoked. Use PropertySet constructor with no arguments instead.");
                }
                value = this.tree.newSet();
            }
            this.addProperty(name, new Value(value, ValueTypes.DATA));
            return value;
        }

        setSet(name: string, index: number, value: PropertySet): Property {
            return this.setProperty(name, index, new Value(value, ValueTypes.DATA));
        }

        setSetByPath(path: any, value: PropertySet): Property {
            return this.setPropertyByPath(path, new Value(value, ValueTypes.DATA))
        }

        getSet(identifier: any, index?: number): PropertySet {
            var property = this.getProperty(identifier, index);
            return !property ? null : property.getSet();
        }

        getSets(name: string): PropertySet[] {
            var values: PropertySet[] = [];
            var array = this.getPropertyArray(name);
            array.forEach((property: Property) => {
                values.push(property.getSet());
            });
            return values;
        }

        // string methods

        addString(name: string, value: string): Property {
            return this.addProperty(name, new Value(value, ValueTypes.STRING));
        }

        addStrings(name: string, values: string[]): Property[] {

            var properties: Property[] = [];
            values.forEach((value: string) => {
                properties.push(this.addString(name, value));
            });
            return properties;
        }

        setString(name: string, index: number, value: string): Property {
            return this.setProperty(name, index, new Value(value, ValueTypes.STRING));
        }

        setStringByPath(path: any, value: string): Property {
            return this.setPropertyByPath(path, new Value(value, ValueTypes.STRING))
        }

        getString(identifier: string, index?: number): string {
            var property = this.getProperty(identifier, index);
            return !property ? null : property.getString();
        }

        getStrings(name: string): string[] {
            var values: string[] = [];
            var array = this.getPropertyArray(name);
            array.forEach((property: Property) => {
                values.push(property.getString());
            });
            return values;
        }

        // TODO: Add methods for each type
    }
}