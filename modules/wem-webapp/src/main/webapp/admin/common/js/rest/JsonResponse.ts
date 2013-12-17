module api_rest {

    export class JsonResponse<T> extends Response {

        private json:any;

        constructor(json:any) {
            super();
            try {
                this.json = JSON.parse(json);
            } catch (e) {
                console.warn("Failed to parse the response", json, e);
            }
        }

        isBlank():boolean {
            return !this.json;
        }

        getJson():any {
            return this.json;
        }

        getResult():T {
            return <T>this.json;
        }
    }
}
