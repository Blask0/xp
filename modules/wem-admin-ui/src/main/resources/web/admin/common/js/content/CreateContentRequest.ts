module api.content {

    export class CreateContentRequest extends ContentResourceRequest<api.content.json.ContentJson, Content> {

        private draft: boolean = false;

        private name: ContentName;

        private parent: ContentPath;

        private contentType: api.schema.content.ContentTypeName;

        private data: api.data.PropertyTree;

        private metadata: Metadata[] = [];

        private displayName: string;

        constructor() {
            super();
            super.setMethod("POST");
        }

        setDraft(value: boolean): CreateContentRequest {
            this.draft = value;
            return this;
        }

        setName(value: ContentName): CreateContentRequest {
            this.name = value;
            return this;
        }

        setParent(value: ContentPath): CreateContentRequest {
            this.parent = value;
            return this;
        }

        setContentType(value: api.schema.content.ContentTypeName): CreateContentRequest {
            this.contentType = value;
            return this;
        }

        setData(data: api.data.PropertyTree): CreateContentRequest {
            this.data = data;
            return this;
        }

        setMetadata(metadata: Metadata[]): CreateContentRequest {
            this.metadata = metadata;
            return this;
        }

        setDisplayName(displayName: string): CreateContentRequest {
            this.displayName = displayName;
            return this;
        }

        getParams(): Object {
            return {
                draft: this.draft,
                name: this.name.isUnnamed() ? this.name.toUnnamed().toStringIncludingHidden() : this.name.toString(),
                parent: this.parent.toString(),
                contentType: this.contentType.toString(),
                data: this.data.toJson(),
                metadata: this.metadataToJson(),
                displayName: this.displayName
            };
        }

        private metadataToJson(): api.content.json.MetadataJson[] {
            return this.metadata ? this.metadata.map((metadata: Metadata) => metadata.toJson()) : null;
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "create");
        }

        sendAndParse(): wemQ.Promise<Content> {

            return this.send().
                then((response: api.rest.JsonResponse<api.content.json.ContentJson>) => {

                    return this.fromJsonToContent(response.getResult());

                });
        }

    }
}