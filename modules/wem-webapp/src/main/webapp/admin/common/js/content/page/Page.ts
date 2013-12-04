module api_content_page{

    export class Page extends BasePageComponent<PageTemplateKey,PageTemplateName> {

        private config:api_data.RootDataSet;

        constructor(builder:PageBuilder) {
            super(builder);
            this.config = builder.config;
        }

        getConfig():api_data.RootDataSet {
            return this.config;
        }
    }

    export class PageBuilder extends BaseComponentBuilder<PageTemplateKey,PageTemplateName>{

        config:api_data.RootDataSet;

        public setConfig(value:api_data.RootDataSet):PageBuilder {
            this.config = value;
            return this;
        }

        public build():Page {
            return new Page(this);
        }
    }
}