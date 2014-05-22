/*
 This code contains a lot of prototype coding at the moment.
 A clean up should be done when Live Edit is specked
 */


module LiveEdit.component.dragdropsort.DragDropSort {

    import ItemType = api.liveedit.ItemType;
    import ItemView = api.liveedit.ItemView;
    import RegionItemType = api.liveedit.RegionItemType;
    import TextItemType = api.liveedit.text.TextItemType;
    import LayoutItemType = api.liveedit.layout.LayoutItemType;
    import DraggableStartEvent = api.liveedit.DraggableStartEvent;
    import DraggableStopEvent = api.liveedit.DraggableStopEvent;
    import SortableStartEvent = api.liveedit.SortableStartEvent;
    import SortableStopEvent = api.liveedit.SortableStopEvent;
    import SortableUpdateEvent = api.liveedit.SortableUpdateEvent;
    import PageComponentDeselectEvent = api.liveedit.PageComponentDeselectEvent;
    import PageComponentAddedEvent = api.liveedit.PageComponentAddedEvent;

    // Uses
    var $ = $liveEdit;

    // jQuery sortable cursor position form to the drag helper.
    var CURSOR_AT: any = {left: 24, top: 24};

    // Set up selectors for jQuery.sortable configuration.
    var REGION_SELECTOR: string = RegionItemType.get().getConfig().getCssSelector();

    var LAYOUT_SELECTOR: string = LayoutItemType.get().getConfig().getCssSelector();

    var CONTEXT_WINDOW_DRAG_SOURCE_SELECTOR: string = '[data-context-window-draggable="true"]';

    var SORTABLE_ITEMS_SELECTOR: string = createSortableItemsSelector();

    var _isDragging: boolean = false;

    export function init(): void {
        createJQueryUiSortable(REGION_SELECTOR);
        registerGlobalListeners();
    }

    export function isDragging(): boolean {
        return _isDragging;
    }

    function disableDragDrop(): void {
        $(REGION_SELECTOR).sortable('disable');
    }

    export function createSortableLayout(component: api.liveedit.ItemView) {
        $(component.getHTMLElement()).find(REGION_SELECTOR).each((index, element) => {
            console.log("Creating jquerysortable for", element);
            createJQueryUiSortable($(element));
        });
    }

    function createJQueryUiSortable(selector): void {
        console.log("Creating jQuery sortable on selector: ", selector, this);
        $(selector).sortable({
            revert: false,
            connectWith: REGION_SELECTOR, //removing this solves the over event not firing bug, not sure what it might break though. it broke dragging out of layouts, now seems to work.
            items: SORTABLE_ITEMS_SELECTOR,
            distance: 20,
            delay: 50,
            tolerance: 'intersect',
            cursor: 'move',
            cursorAt: CURSOR_AT,
            scrollSensitivity: calculateScrollSensitivity(),
            placeholder: 'live-edit-drop-target-placeholder',
            zIndex: 1001001,
            helper: (event, helper) => LiveEdit.component.helper.DragHelper.createDragHelperHtml(),
            start: (event, ui) => handleSortStart(event, ui),
            over: (event, ui) => handleDragOver(event, ui),
            out: (event, ui) => handleDragOut(event, ui),
            change: (event, ui) => handleSortChange(event, ui),
            receive: (event, ui) => handleReceive(event, ui),
            update: (event, ui) => handleSortUpdate(event, ui),
            stop: (event, ui) => handleSortStop(event, ui)
        });
//        $(selector).on('mouseover', (event) => {
//            if ($(event.currentTarget).hasClass("ui-sortable")) {
//                if (draggingUI) {
//                    this.handleDragOver(event, draggingUI);
//                }
//            }
//
//        });
    }

    function updateScrollSensitivity(selector): void {
        var scrollSensitivity = calculateScrollSensitivity();
        $(selector).sortable('option', 'scrollSensitivity', scrollSensitivity);
    }

    function calculateScrollSensitivity(): number {
        // use getViewPortSize() instead of document.body.clientHeight which returned the height of the whole rendered page, not just of the part visible in LiveEdit
        var height = LiveEdit.DomHelper.getViewPortSize().height;
        var scrollSensitivity = Math.round(height / 8);
        scrollSensitivity = Math.max(20, Math.min(scrollSensitivity, 100));
        return scrollSensitivity
    }

    // Used by the Context Window when dragging above the IFrame
    export function createJQueryUiDraggable(contextWindowItem: JQuery): void {
        contextWindowItem.draggable({
            connectToSortable: REGION_SELECTOR,
            addClasses: false,
            cursor: 'move',
            appendTo: 'body',
            zIndex: 5100000,
            cursorAt: CURSOR_AT,
            helper: () => {
                return LiveEdit.component.helper.DragHelper.createDragHelperHtml();
            },
            start: (event, ui) => {
                new DraggableStartEvent().fire();
                _isDragging = true;
            },
            stop: (event, ui) => {
                new DraggableStopEvent().fire();
                _isDragging = false;
            }
        });
    }

    function refreshSortable(): void {
        $(REGION_SELECTOR).sortable('refresh');
    }

    function targetIsPlaceholder(target: JQuery): Boolean {
        return target.hasClass('live-edit-drop-target-placeholder')
    }

    function handleSortStart(event: JQueryEventObject, ui): void {
        updateScrollSensitivity(event.target);
        _isDragging = true;

        var component = ItemView.fromJQuery(ui.item);

        // Temporary store the selection info during the drag drop lifecycle.
        // Data is nullified on drag stop.
        ui.item.data('live-edit-selected-on-sort-start', component.isSelected());

        ui.placeholder.html(LiveEdit.PlaceholderCreator.createPlaceholderForJQuerySortable(component));

        refreshSortable();

        new SortableStartEvent().fire();
    }

    function handleDragOver(event: JQueryEventObject, ui): void {
        event.stopPropagation();

        var component = ItemView.fromJQuery(ui.item);

        var isDraggingOverLayoutComponent = ui.placeholder.closest(LAYOUT_SELECTOR).length > 0;

        if (component.getType().equals(LayoutItemType.get()) && isDraggingOverLayoutComponent) {
            LiveEdit.component.helper.DragHelper.updateStatusIcon(false);
            ui.placeholder.hide();
        } else {
            LiveEdit.component.helper.DragHelper.updateStatusIcon(true);
            $(window).trigger('sortableOver.liveEdit', [event, ui]);
        }
    }

    function handleDragOut(event: JQueryEventObject, ui): void {
        if (targetIsPlaceholder($(event.target))) {
            removePaddingFromLayoutComponent();
        }
        LiveEdit.component.helper.DragHelper.updateStatusIcon(false);

        $(window).trigger('sortableOut.liveEdit', [event, ui]);
    }

    function handleSortChange(event: JQueryEventObject, ui): void {
        var component = ItemView.fromJQuery($(event.target));

        addPaddingToLayoutComponent(component);
        LiveEdit.component.helper.DragHelper.updateStatusIcon(true);

        ui.placeholder.show(null);

        $(window).trigger('sortableChange.liveEdit', [event, ui]);
    }

    function handleSortUpdate(event: JQueryEventObject, ui): void {
        var component = ItemView.fromJQuery(ui.item);

        if (component.hasComponentPath()) {
            new SortableUpdateEvent(component).fire();
        }
    }

    function handleSortStop(event: JQueryEventObject, ui): void {
        _isDragging = false;

        var component = ItemView.fromJQuery(ui.item);

        removePaddingFromLayoutComponent();

        var draggedItemIsLayoutComponent = component.getType().equals(LayoutItemType.get()),
            targetComponentIsInLayoutComponent = $(event.target).closest(LAYOUT_SELECTOR).length > 0;

        if (draggedItemIsLayoutComponent && targetComponentIsInLayoutComponent) {
            ui.item.remove()
        }

        if (LiveEdit.DomHelper.supportsTouch()) {
            $(window).trigger('mouseOutComponent.liveEdit');
        }

        var wasSelectedOnDragStart = component.getElement().data('live-edit-selected-on-drag-start');

        new SortableStopEvent(component).fire();

        component.getElement().removeData('live-edit-selected-on-drag-start');
    }

    // When sortable receives a new item
    function handleReceive(event: JQueryEventObject, ui): void {
        if (isItemDraggedFromContextWindow(ui.item)) {
            var droppedComponent = $(event.target).children(CONTEXT_WINDOW_DRAG_SOURCE_SELECTOR);
            var itemType = ItemType.byShortName(droppedComponent.data('live-edit-type'));
            var emptyComponent = LiveEdit.component.ComponentPlaceholder.fromComponent(itemType);

            droppedComponent.replaceWith(emptyComponent.getHTMLElement());
            emptyComponent.init();

            //$(window).trigger('sortableUpdate.liveEdit');

            // The layout padding is removed on sortStop, but this is not fired yet at this point
            // Remove it now so the auto selection is properly aligned.
            removePaddingFromLayoutComponent();

            var region = emptyComponent.getParentRegion();
            new PageComponentAddedEvent().
                setComponent(emptyComponent).
                setRegion(region.getRegionName()).
                setPrecedingComponent(emptyComponent.getPrecedingComponentPath()).
                fire();
            LiveEdit.component.Selection.handleSelect(emptyComponent);
        }
    }

    function isItemDraggedFromContextWindow(item: JQuery): boolean {
        var isDraggedFromContextWindow: boolean = item.data('context-window-draggable');
        return isDraggedFromContextWindow != undefined && isDraggedFromContextWindow == true;
    }

    function addPaddingToLayoutComponent(component: ItemView): void {
        component.getElement().closest(LAYOUT_SELECTOR).addClass('live-edit-component-padding');
    }


    function removePaddingFromLayoutComponent(): void {
        $('.live-edit-component-padding').removeClass('live-edit-component-padding');
    }

    function registerGlobalListeners(): void {
        PageComponentDeselectEvent.on(() => {
            if (LiveEdit.DomHelper.supportsTouch() && !_isDragging) {
                disableDragDrop();
            }
        });

        $(window).on('selectTextComponent.liveEdit', () => {
            $(REGION_SELECTOR).sortable('option', 'cancel', TextItemType.get().getConfig().getCssSelector());
        });

        $(window).on('leaveTextComponent.liveEdit', () => {
            $(REGION_SELECTOR).sortable('option', 'cancel', '');
        });
    }

    function createSortableItemsSelector(): string {

        var sortableItemsSelector: string[] = [];
        ItemType.getDraggables().forEach((draggableItemType: ItemType) => {
            sortableItemsSelector.push(draggableItemType.getConfig().getCssSelector());
        });

        return sortableItemsSelector.toString();
    }

}