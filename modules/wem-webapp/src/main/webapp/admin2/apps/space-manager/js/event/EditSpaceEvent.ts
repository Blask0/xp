module app_event {
    export class EditSpaceEvent extends BaseSpaceModelEvent {

        constructor(model:app_model.SpaceModel[]) {
            super('editSpaceEvent', model);
        }

        static on(handler:(event:EditSpaceEvent) => void) {
            api_event.onEvent('editSpaceEvent', handler);
        }
    }
}
