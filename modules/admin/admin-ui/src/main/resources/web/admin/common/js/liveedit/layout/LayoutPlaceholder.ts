module api.liveedit.layout {

    import LayoutComponent = api.content.page.region.LayoutComponent;
    import PageItemType = api.liveedit.PageItemType;
    import SiteModel = api.content.site.SiteModel;
    import LayoutDescriptor = api.content.page.region.LayoutDescriptor;
    import LayoutDescriptorComboBox = api.content.page.region.LayoutDescriptorComboBox;
    import SelectedOptionEvent = api.ui.selector.combobox.SelectedOptionEvent;

    export class LayoutPlaceholder extends ItemViewPlaceholder {

        private comboBox: api.content.page.region.LayoutDescriptorComboBox;

        private layoutComponentView: LayoutComponentView;

        constructor(layoutView: LayoutComponentView) {
            super();
            this.addClassEx("layout-placeholder");
            this.layoutComponentView = layoutView;


            this.comboBox = new LayoutDescriptorComboBox();
            this.comboBox.loadDescriptors(layoutView.getLiveEditModel().getSiteModel().getApplicationKeys());

            this.appendChild(this.comboBox);

            this.comboBox.onOptionSelected((event: SelectedOptionEvent<LayoutDescriptor>) => {
                this.layoutComponentView.showLoadingSpinner();
                var descriptor = event.getSelectedOption().getOption().displayValue;

                var layoutComponent: LayoutComponent = this.layoutComponentView.getComponent();
                layoutComponent.setDescriptor(descriptor.getKey(), descriptor);
            });

            var siteModel = layoutView.getLiveEditModel().getSiteModel();
            siteModel.onApplicationAdded(() => this.reloadDescriptorsOnApplicationChange(siteModel));
            siteModel.onApplicationRemoved(() => this.reloadDescriptorsOnApplicationChange(siteModel));
        }

        private reloadDescriptorsOnApplicationChange(siteModel: SiteModel) {
            this.comboBox.loadDescriptors(siteModel.getApplicationKeys());
        }

        select() {
            this.comboBox.show();
            this.comboBox.giveFocus();
        }

        deselect() {
            this.comboBox.hide();
        }
    }
}