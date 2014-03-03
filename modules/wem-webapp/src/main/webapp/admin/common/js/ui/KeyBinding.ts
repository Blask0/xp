module api.ui {

    export class KeyBinding {

        private combination: string;

        private callback: (e: ExtendedKeyboardEvent, combo: string) => boolean;

        private action: string;

        private global: boolean;

        constructor(combination: string, callback?: (e: ExtendedKeyboardEvent, combo: string) => any, action?: string, global?: boolean) {

            this.combination = combination;
            this.callback = callback;
            this.action = action;
            this.global = global;
        }

        setCallback(func: (e: ExtendedKeyboardEvent, combo: string) => boolean): KeyBinding {
            this.callback = func;
            return this;
        }

        setAction(value: string): KeyBinding {
            this.action = value;
            return this;
        }

        setGlobal(global: boolean): KeyBinding {
            this.global = global;
            return this;
        }

        getCombination(): string {
            return this.combination;
        }

        getCallback(): (e: ExtendedKeyboardEvent, combo: string) => boolean {
            return this.callback;
        }

        getAction(): string {
            return this.action;
        }

        isGlobal(): boolean {
            return this.global;
        }

        static newKeyBinding(combination: string): KeyBinding {
            return new KeyBinding(combination);
        }
    }


}