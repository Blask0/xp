module api_ui {

    export interface ImageUploaderListener extends api_event.Listener {

        onFileUploaded:(uploadItem:UploadItem) => void;

        onUploadComplete:() => void;

    }
}