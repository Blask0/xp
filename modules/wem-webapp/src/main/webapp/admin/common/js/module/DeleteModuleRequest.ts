module api_module {

    export class DeleteModuleRequest extends ModuleResourceRequest<api_module_json.ModuleJson> {

        private key:string;

        constructor(key:string) {
            super();
            super.setMethod("POST");
            this.key = key;
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "delete");
        }

        getParams():Object {
            return {
                key: this.key
            };
        }
    }
}