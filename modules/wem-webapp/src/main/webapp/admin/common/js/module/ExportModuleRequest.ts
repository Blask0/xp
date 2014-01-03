module api.module {

    export class ExportModuleRequest extends ModuleResourceRequest<any> {

        private moduleKey:ModuleKey;

        constructor(moduleKey:ModuleKey) {
            super();
            super.setMethod("GET");
            this.moduleKey = moduleKey;
        }

        getParams():Object {
            return {
                moduleKey: this.moduleKey.toString()
            };
        }

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "export");
        }
    }
}