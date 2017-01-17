module api.content.page {

    export class GetPageDescriptorsByApplicationRequest extends PageDescriptorResourceRequest<PageDescriptorsJson, PageDescriptor[]> {

        private applicationKey: api.application.ApplicationKey;

        constructor(applicationKey: api.application.ApplicationKey) {
            super();
            super.setMethod('GET');
            this.applicationKey = applicationKey;
        }

        getParams(): Object {
            return {
                applicationKey: this.applicationKey.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'list', 'by_application');
        }

        sendAndParse(): wemQ.Promise<PageDescriptor[]> {

            let cached = this.cache.getByApplication(this.applicationKey);
            if (cached) {
                return wemQ(cached);
            } else {
                return this.send().then((response: api.rest.JsonResponse<PageDescriptorsJson>) => {
                    return this.fromJsonToPageDescriptors(response.getResult());
                });
            }
        }
    }
}
