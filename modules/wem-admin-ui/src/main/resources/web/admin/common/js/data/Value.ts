module api.data {

    export class Value implements api.Equitable, api.Cloneable {

        private type: ValueType;

        private value: Object = null;

        constructor(value: Object, type: ValueType) {
            this.value = value;
            this.type = type;
            if (value) {
                var isValid = this.type.isValid(value);
                if (isValid == undefined) {
                    throw new Error(api.ClassHelper.getClassName(this.type) + ".isValid() did not return any value: " + isValid);
                }
                if (isValid == false) {
                    throw new Error("Invalid value for type " + type.toString() + ": " + value);
                }
            }
        }

        getType(): ValueType {
            return this.type;
        }

        isNotNull(): boolean {
            return !this.isNull();
        }

        isNull(): boolean {
            return this.value == null || this.value == undefined;
        }

        getObject(): Object {
            return this.value;
        }

        getString(): string {
            if (this.isNull()) {
                return null;
            }
            return this.type.valueToString(this);
        }

        isPropertySet(): boolean {
            return ValueTypes.DATA.toString() == this.type.toString();
        }

        getPropertySet(): PropertySet {
            if (this.isNull()) {
                return null;
            }

            api.util.assert(api.ObjectHelper.iFrameSafeInstanceOf(this.value, PropertySet),
                "Expected value to be a PropertySet: " + api.ClassHelper.getClassName(this.value));

            return <PropertySet>this.value;
        }

        getBoolean(): boolean {
            if (this.isNull()) {
                return null;
            }
            return this.type.valueToBoolean(this);
        }

        getNumber(): number {
            if (this.isNull()) {
                return null;
            }
            return this.type.valueToNumber(this);
        }

        getDate(): Date {
            if (this.isNull()) {
                return null;
            }
            return <Date>this.value;
        }

        getGeoPoint(): api.util.GeoPoint {
            if (this.isNull()) {
                return null;
            }
            return <api.util.GeoPoint>this.value;
        }

        getBinaryReference(): api.util.BinaryReference {
            if (this.isNull()) {
                return null;
            }
            return <api.util.BinaryReference>this.value;
        }

        getReference(): api.util.Reference {
            if (this.isNull()) {
                return null;
            }
            return <api.util.Reference>this.value;
        }

        getLocalTime(): api.util.LocalTime {
            if (this.isNull()) {
                return null;
            }
            return <api.util.LocalTime>this.value;
        }

        getContentId(): api.content.ContentId {
            if (this.isNull()) {
                return null;
            }
            return <api.content.ContentId>this.value;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Value)) {
                return false;
            }

            var other = <Value>o;

            if (!api.ObjectHelper.equals(this.type, other.type)) {
                return false;
            }

            return this.type.valueEquals(this.value, other.value);
        }

        clone(): Value {

            return new Value(this.value, this.type);
        }
    }
}