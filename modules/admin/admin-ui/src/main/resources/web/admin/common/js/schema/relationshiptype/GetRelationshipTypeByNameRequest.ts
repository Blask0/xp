module api.schema.relationshiptype {

    export class GetRelationshipTypeByNameRequest extends RelationshipTypeResourceRequest<RelationshipTypeJson, RelationshipType> {

        private name: RelationshipTypeName;

        constructor(name: RelationshipTypeName) {
            super();
            super.setMethod("GET");
            this.name = name;
        }

        getParams(): Object {
            return {
                name: this.name.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return super.getResourcePath();
        }

        sendAndParse(): wemQ.Promise<RelationshipType> {

            var relationshipTypeCache = RelationshipTypeCache.get();
            var relationshipType = relationshipTypeCache.getByKey(this.name);

            if (relationshipType) {
                return wemQ(relationshipType);
            } else if (relationshipTypeCache.isOnLoading(this.name)) {
                return relationshipTypeCache.getOnLoaded(this.name);
            } else {
                relationshipTypeCache.addToLoading(this.name);
            }

            return this.send().then((response: api.rest.JsonResponse<RelationshipTypeJson>) => {
                var relationshipType = this.fromJsonToReleationshipType(response.getResult());
                relationshipTypeCache.put(relationshipType);
                return relationshipType;
            });
        }
    }
}