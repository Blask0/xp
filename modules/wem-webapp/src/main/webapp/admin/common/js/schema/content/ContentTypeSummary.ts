module api_schema_content{

    export class ContentTypeSummary extends api_schema.Schema {

        private allowChildContent:boolean;

        private abstract:boolean;

        private final:boolean;

        private superType:api_schema_content.ContentTypeName;

        private contentDisplayNameScript:string;

        private modifier:string;

        private owner:string;

        constructor(json:api_schema_content_json.ContentTypeSummaryJson) {
            super(json);
            this.allowChildContent = json.allowChildContent;
            this.final = json.final;
            this.abstract = json.abstract;
            this.superType = new api_schema_content.ContentTypeName(json.superType);
            this.contentDisplayNameScript = json.contentDisplayNameScript;
            this.owner = json.owner;
            this.modifier = json.modifier;
        }

        getContentTypeName(): api_schema_content.ContentTypeName {
            return new api_schema_content.ContentTypeName(this.getName());
        }

        isFinal():boolean {
            return this.final;
        }

        isAbstract():boolean {
            return this.abstract;
        }

        isAllowChildContent():boolean {
            return this.allowChildContent;
        }

        getSuperType():api_schema_content.ContentTypeName {
            return this.superType;
        }

        hasContentDisplayNameScript(): boolean {
            return this.contentDisplayNameScript != null;
        }

        getContentDisplayNameScript():string {
            return this.contentDisplayNameScript;
        }

        getOwner():string {
            return this.owner;
        }

        getModifier():string {
            return this.modifier;
        }

        static fromJsonArray(jsonArray:api_schema_content_json.ContentTypeSummaryJson[]):ContentTypeSummary[] {
            var array:ContentTypeSummary[] = [];

            jsonArray.forEach( (summaryJson:api_schema_content_json.ContentTypeSummaryJson) => {
                array.push(new ContentTypeSummary(summaryJson));
            } );
            return array;
        }

    }
}