module api.form {

    export class InputTypeName implements api.Equitable {

        private static CUSTOM_PREFIX: string = "custom:";

        private custom: boolean;

        private name: string;

        private refString: string;

        static parseInputTypeName(str: string) {
            if (str.substr(0, InputTypeName.CUSTOM_PREFIX.length) == InputTypeName.CUSTOM_PREFIX) {
                return new InputTypeName(str.substr(InputTypeName.CUSTOM_PREFIX.length, str.length), true);
            }
            else {
                return new InputTypeName(str, false);
            }
        }

        constructor(name: string, custom: boolean) {
            this.name = name;
            this.custom = custom;

            if (this.custom) {
                this.refString = InputTypeName.CUSTOM_PREFIX + name;
            }
            else {
                this.refString = name;
            }
        }

        getName(): string {
            return this.name;
        }

        isBuiltIn(): boolean {
            return !this.custom;
        }

        toString(): string {
            return this.refString;
        }

        public toJson(): api.form.json.InputTypeJson {

            return <api.form.json.InputTypeJson>{
                name: this.toString()
            };
        }

        equals(o: api.Equitable): boolean {

            if (!(o instanceof InputTypeName)) {
                return false;
            }

            var other = <InputTypeName>o;

            if (!api.EquitableHelper.booleanEquals(this.custom, other.custom)) {
                return false;
            }

            if (!api.EquitableHelper.stringEquals(this.name, other.name)) {
                return false;
            }

            return true;
        }
    }
}
