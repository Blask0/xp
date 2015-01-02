module api.liveedit {

    export class ItemType implements api.Equitable {

        static DATA_ATTRIBUTE = "live-edit-type";

        private static shortNameToInstance: {[shortName: string]: ItemType} = {};

        private shortName: string;

        private config: ItemTypeConfig;

        constructor(shortName: string, config: ItemTypeConfigJson) {
            ItemType.shortNameToInstance[shortName] = this;
            this.shortName = shortName;
            this.config = new ItemTypeConfig(config);
        }

        getShortName(): string {
            return this.shortName;
        }

        getConfig(): ItemTypeConfig {
            return this.config;
        }


        isPageComponentType(): boolean {
            return false
        }

        toPageComponentType(): api.content.page.ComponentType {
            api.util.assert(this.isPageComponentType(), "Not support when ItemType is not a ComponentType");
            return api.content.page.ComponentType.byShortName(this.shortName);
        }

        createView(config: CreateItemViewConfig<ItemView,any>): ItemView {
            throw new Error("Must be implemented by inheritors");
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ItemType)) {
                return false;
            }

            var other = <ItemType>o;

            if (!api.ObjectHelper.stringEquals(this.shortName, other.shortName)) {
                return false;
            }

            return true;
        }

        static getDraggables(): ItemType[] {
            var draggables: ItemType[] = [];
            for (var shortName in  ItemType.shortNameToInstance) {
                var itemType = ItemType.shortNameToInstance[shortName];
                if (itemType.getConfig().isDraggable()) {
                    draggables.push(itemType);
                }
            }
            return draggables;
        }

        static byShortName(shortName: string): ItemType {
            return ItemType.shortNameToInstance[shortName];
        }

        static fromHTMLElement(element: HTMLElement): ItemType {
            var typeAsString = element.getAttribute("data-" + ItemType.DATA_ATTRIBUTE);
            return ItemType.byShortName(typeAsString);
        }

        static fromElement(element: api.dom.Element): ItemType {
            var typeAsString = element.getEl().getAttribute("data-" + ItemType.DATA_ATTRIBUTE);
            return ItemType.byShortName(typeAsString);
        }
    }
}