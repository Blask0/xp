module api.content.page.layout {

    import ModuleKey = api.module.ModuleKey;

    export class GetLayoutDescriptorByKeyRequest extends LayoutDescriptorResourceRequest<LayoutDescriptorJson,LayoutDescriptor> {

        private key: api.content.page.DescriptorKey;

        constructor(key: api.content.page.DescriptorKey) {
            super();
            this.key = key;
        }

        setKey(key: api.content.page.DescriptorKey) {
            this.key = key;
        }

        getParams(): Object {
            throw new Error("Unexpected call");
        }

        getRequestPath(): api.rest.Path {
            throw new Error("Unexpected call");
        }

        sendAndParse(): wemQ.Promise<LayoutDescriptor> {
            var deferred = wemQ.defer<LayoutDescriptor>();

            new GetLayoutDescriptorsByModuleRequest(this.key.getModuleKey()).sendAndParse().then((descriptors: LayoutDescriptor[]) => {
                descriptors.forEach((descriptor: LayoutDescriptor) => {
                    if (this.key.equals(descriptor.getKey())) {
                        deferred.resolve(descriptor);
                    }
                });
            }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();

            return deferred.promise;
        }
    }
}