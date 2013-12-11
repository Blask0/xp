module app_contextwindow_image {
    export class ImageSelectPanel extends api_ui.Panel {

        private image:api_content_page_image.ImageComponent;

        private contextWindow:app_contextwindow.ContextWindow;

        private comboBox:api_ui_combobox.ComboBox<api_content.ContentSummary>;

        private selectedOptionsView:ImageSelectPanelSelectedOptionsView;

        private templatePanel:api_ui.Panel;

        private recentPanel:RecentPanel;

        private deck:api_ui.DeckPanel;

        private selectedOption:api_ui_combobox.Option<api_content.ContentSummary>;

        private liveEditItems:{[key: number]: api_content.ContentSummary
        };

        private liveEditIndex:number = 1;

        constructor(contextWindow:app_contextwindow.ContextWindow) {
            super("ImageSelectPanel");
            this.addClass("select-panel");
            var comboBoxWrapper = new api_dom.DivEl();
            this.contextWindow = contextWindow;

            this.liveEditItems = {};

            this.selectedOptionsView = new ImageSelectPanelSelectedOptionsView();
            this.selectedOptionsView.hide();
            this.comboBox = this.createComboBox();
            this.comboBox.addSelectedOptionRemovedListener(() => {
                this.contextWindow.getLiveEditWindow().LiveEdit.component.dragdropsort.EmptyComponent.restoreEmptyComponent();
                this.itemRemoved();
            });


            this.deck = new api_ui.DeckPanel();

            this.recentPanel = new RecentPanel();

            this.templatePanel = new api_ui.Panel("TemplatePanel", "template-panel");
            this.templatePanel.getEl().setInnerHtml("Template goes here");

            this.deck.addPanel(this.recentPanel);
            this.deck.addPanel(this.templatePanel);

            this.deck.showPanel(0);


            comboBoxWrapper.appendChild(this.comboBox);
            comboBoxWrapper.appendChild(this.selectedOptionsView);
            this.appendChild(comboBoxWrapper);
            this.appendChild(this.deck);

            app_contextwindow.ComponentSelectEvent.on((event) => {
                //TODO: set image here
                if (!event.getComponent().isEmpty()) {
                    this.image = new api_content_page_image.ImageComponent();
                    this.itemSelected();
                    if (event.getComponent().getItemId()) {
                        console.log("itemId:", event.getComponent().getItemId());
                        var itemId = event.getComponent().getItemId();
                        this.setSelectedContent(this.liveEditItems[itemId]);
                    }
                } else if (this.selectedOption != null) {
                    this.comboBox.removeSelectedItem(this.selectedOption, true);
                }


            });

            app_contextwindow.ComponentDeselectEvent.on((event) => {
                this.itemRemoved();
            });

            app_contextwindow.ComponentRemovedEvent.on((event) => {
                if (this.selectedOption != null) {
                    this.comboBox.removeSelectedItem(this.selectedOption);
                    this.itemRemoved();
                }
            });

            this.addGridListeners();
        }

        private addGridListeners() {
            this.recentPanel.getGrid().setOnClick((event, data:api_ui_grid.GridOnClickData) => {
                var option = <api_ui_combobox.Option<api_content.ContentSummary>> {
                    //TODO: what is value used for??
                    value: "test",
                    displayValue: this.recentPanel.getDataView().getItem(data.row)
                };

                this.comboBox.selectOption(option);
            });
        }

        private itemSelected() {
            this.selectedOptionsView.show();
            this.deck.showPanel(1);
        }

        private itemRemoved() {
            this.selectedOptionsView.hide();
            this.deck.showPanel(0);
        }

        private createComboBox():api_ui_combobox.ComboBox<api_content.ContentSummary> {

            var comboBoxConfig = <api_ui_combobox.ComboBoxConfig<api_content.ContentSummary>> {
                rowHeight: 50,
                maximumOccurrences: 1,
                optionFormatter: this.optionFormatter,
                selectedOptionsView: this.selectedOptionsView,
                hideComboBoxWhenMaxReached: true
            };

            var comboBox = new api_ui_combobox.ComboBox("imagePicker", comboBoxConfig);

            comboBox.addSelectedOptionRemovedListener(()=> {
                this.selectedOption = null;
                console.log("On selected option removed");
            });
            comboBox.addListener({
                onInputValueChanged: (oldValue, newValue, grid) => {
                    contentSummaryLoader.search(newValue);
                },
                onOptionSelected: (item:api_ui_combobox.Option<api_content.ContentSummary>) => {
                    console.log("On option selected");
                    //TODO: Mocked live use of image
                    this.selectedOption = item;
                    this.contextWindow.getLiveEditWindow().LiveEdit.component.dragdropsort.EmptyComponent.loadComponent('10070', this.liveEditIndex, item.displayValue.getIconUrl());
                    this.liveEditItems[this.liveEditIndex] = item.displayValue;
                    this.itemSelected();
                    this.liveEditIndex++;
                }
            });

            var contentSummaryLoader = new api_form_inputtype_content.ContentSummaryLoader();
            contentSummaryLoader.setAllowedContentTypes(["image"]);
            contentSummaryLoader.addListener({
                onLoading: () => {
                    comboBox.setLabel("Searching...");
                },
                onLoaded: (contentSummaries:api_content.ContentSummary[]) => {
                    var options = this.createOptions(contentSummaries);
                    comboBox.setOptions(options);
                }
            });

            contentSummaryLoader.search("");

            return comboBox;
        }

        private createOptions(contents:api_content.ContentSummary[]):api_ui_combobox.Option<api_content.ContentSummary>[] {
            var options = [];
            contents.forEach((content:api_content.ContentSummary) => {
                options.push({
                    value: content.getId(),
                    displayValue: content
                });
            });
            return options;
        }

        private optionFormatter(row:number, cell:number, content:api_content.ContentSummary, columnDef:any, dataContext:api_ui_combobox.Option<api_content.ContentSummary>):string {
            var img = new api_dom.ImgEl();
            img.setClass("icon");
            img.getEl().setSrc(content.getIconUrl());

            var contentSummary = new api_dom.DivEl();
            contentSummary.setClass("content-summary");

            var displayName = new api_dom.DivEl();
            displayName.setClass("display-name");
            displayName.getEl().setAttribute("title", content.getDisplayName());
            displayName.getEl().setInnerHtml(content.getDisplayName());

            var path = new api_dom.DivEl();
            path.setClass("path");
            path.getEl().setAttribute("title", content.getPath().toString());
            path.getEl().setInnerHtml(content.getPath().toString());

            contentSummary.appendChild(displayName);
            contentSummary.appendChild(path);

            return img.toString() + contentSummary.toString();
        }

        private setSelectedContent(content:api_content.ContentSummary, removeCurrent:boolean = true) {
            api_util.assertNotNull(content, "Cannot set content null");
            var option:api_ui_combobox.Option<api_content.ContentSummary> = {
                value: content.getId(),
                displayValue: content
            };
            if (this.selectedOption != null) {
                this.comboBox.removeSelectedItem(this.selectedOption, true);

            }
            this.selectedOption = option;
            this.comboBox.selectOption(this.selectedOption, true);
        }

        setImage(image:api_content_page_image.ImageComponent) {
            this.image = image;
            this.refreshUI();
        }

        private refreshUI() {
            new api_content.GetContentByIdRequest(this.image.getImageContentId())
                .send()
                .done((jsonResponse:api_rest.JsonResponse<api_content_json.ContentSummaryJson>) => {
                    this.setSelectedContent(new api_content.ContentSummary(jsonResponse.getResult()))
                });
        }
    }
}

