module app.wizard {
    export class LiveFormPanel extends api.ui.Panel {

        private frame: api.dom.IFrameEl;

        private url: string;

        private site: api.content.Content;

        constructor(site: api.content.Content) {
            super("live-form-panel");
            this.url = api.util.getUri("portal/edit/" + site.getContentId().toString());
            this.site = site;
        }

        onElementShown() {
            super.onElementShown();
            this.doLoad();
        }

        renderNew() {

        }

        private doLoad() {
            if (!this.frame) {
                this.frame = new api.dom.IFrameEl();
                this.frame.addClass("live-edit-frame");
                this.frame.setSrc(this.url);
                this.appendChild(this.frame);

                // Wait for iframe to be loaded before adding context window!
                var maxIterations = 10;
                var iterations = 0;
                var intervalId = setInterval(() => {
                    if (this.frame.isLoaded()) {
                        if (this.frame.getHTMLElement()["contentWindow"].$liveEdit) {
                            var contextWindow = new app.contextwindow.ContextWindow({liveEditEl: this.frame, site: this.site});
                            this.appendChild(contextWindow);
                            //contextWindow.init();
                            clearInterval(intervalId);
                        }
                    }
                    iterations++;
                    if (iterations >= maxIterations) {
                        clearInterval(intervalId);
                    }
                }, 200);
            }
        }

        renderExisting(content: api.content.Content, pageTemplate: api.content.page.PageTemplate) {

            if (content.isPage() && pageTemplate != null) {
                var page = content.getPage();

                var regions = pageTemplate.getRegions();
                if (page.hasRegions()) {
                    regions = page.getRegions();
                }

                var headerRegion = regions.getRegion("main");
                console.log("headerRegion", headerRegion);

                var fancyImageComponent = regions.getComponent(new api.content.page.ComponentName("FancyImage"));
                console.log("fancyImageComponent", fancyImageComponent);


                // TODO: live edit render request to server
            }
        }

        public getRegions(): api.content.page.PageRegions {

            var pageRegions = new api.content.page.PageRegionsBuilder();

            // Header region
            var headerRegion = new api.content.page.region.RegionBuilder();
            headerRegion.setName("header");
            var partInHeader = new api.content.page.part.PartComponentBuilder().
                setName(new api.content.page.ComponentName("PartInHeader")).
                setTemplate(api.content.page.part.PartTemplateKey.fromString("Blueman-1.0.0|demo-1.0.0|my-part")).
                build();
            headerRegion.addComponent(partInHeader);
            pageRegions.addRegion(headerRegion.build());

            // Main region
            var mainRegion = new api.content.page.region.RegionBuilder();
            mainRegion.setName("main");
            var fancyImage = new api.content.page.image.ImageComponentBuilder().
                setImage(new api.content.ContentId("123")).
                setName(new api.content.page.ComponentName("FancyImage")).
                setTemplate(api.content.page.image.ImageTemplateKey.fromString("Blueman-1.0.0|demo-1.0.0|fancy-image")).
                build();
            mainRegion.addComponent(fancyImage);
            pageRegions.addRegion(mainRegion.build());

            // Footer region
            var footerRegion = new api.content.page.region.RegionBuilder();
            footerRegion.setName("footer");

            var twoColumnsLeftRegion = new api.content.page.region.RegionBuilder().
                setName("twoColumnsLeftRegion").
                addComponent(new api.content.page.part.PartComponentBuilder().
                    setName(new api.content.page.ComponentName("PartInTwoColumnLeft")).
                    setTemplate(api.content.page.part.PartTemplateKey.fromString("Blueman-1.0.0|demo-1.0.0|my-part")).
                    build()).
                build();
            var twoColumnsRightRegion = new api.content.page.region.RegionBuilder().
                setName("twoColumnsRightRegion").
                addComponent(new api.content.page.part.PartComponentBuilder().
                    setName(new api.content.page.ComponentName("PartInTwoColumnRight")).
                    setTemplate(api.content.page.part.PartTemplateKey.fromString("Blueman-1.0.0|demo-1.0.0|my-part")).
                    build()).
                build();
            var twoColumnsRegions = new api.content.page.layout.LayoutRegionsBuilder().
                addRegion(twoColumnsLeftRegion).
                addRegion(twoColumnsRightRegion).
                build();
            var twoColumns = new api.content.page.layout.LayoutComponentBuilder().
                setRegions(twoColumnsRegions).
                setName(new api.content.page.ComponentName("FooterTwoColumns")).
                setTemplate(api.content.page.layout.LayoutTemplateKey.fromString("Blueman-1.0.0|demo-1.0.0|two-columns")).
                build();
            footerRegion.addComponent(twoColumns);

            pageRegions.addRegion(footerRegion.build());

            return pageRegions.build();
        }
    }
}