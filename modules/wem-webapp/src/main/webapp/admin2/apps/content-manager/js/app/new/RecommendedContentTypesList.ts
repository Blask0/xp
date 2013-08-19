module app_new {

    export class RecommendedContentTypesList extends api_dom.DivEl implements api_ui.Observable {

        private contentTypesList:ContentTypesList;

        constructor(className?:string) {
            super("RecommendedContentTypesList", className);

            var h4 = new api_dom.H4El();
            h4.getEl().setInnerHtml("Recommended");
            this.appendChild(h4);

            this.contentTypesList = new ContentTypesList();
            this.appendChild(this.contentTypesList);
        }

        addListener(listener:ContentTypesListListener) {
            this.contentTypesList.addListener(listener);
        }

        removeListener(listener:ContentTypesListListener) {
            this.contentTypesList.removeListener(listener);
        }

        refresh() {
            var recommendedArray:string[] = RecentContentTypes.get().getRecommendedContentTypes();

            // service returns error if empty array is passed
            if (recommendedArray.length > 0) {
                api_remote_contenttype.RemoteContentTypeService.contentType_get(
                    {
                        qualifiedNames: recommendedArray,
                        format: "json"
                    },
                    (result:api_remote_contenttype.GetResult) => {

                        var newContentTypeArray:api_remote_contenttype.ContentType[] = [];
                        result.contentTypes.forEach((contentType:api_remote_contenttype.ContentType) => {
                            newContentTypeArray.push(contentType);
                        });

                        this.contentTypesList.setContentTypes(newContentTypeArray);
                    });
            }

        }
    }
}