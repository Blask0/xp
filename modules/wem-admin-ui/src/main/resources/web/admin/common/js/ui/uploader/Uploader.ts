module api.ui.uploader {

    import Button = api.ui.button.Button;
    import CloseButton = api.ui.button.CloseButton;

    export class PluploadStatus {
        public static QUEUED = plupload.QUEUED;
        public static UPLOADING = plupload.UPLOADING;
        public static FAILED = plupload.FAILED;
        public static DONE = plupload.DONE;
    }

    export interface PluploadFile {
        id: string;
        name: string;
        percent: number;
        type: string;
        size: number;
        origSize: number;
        loaded: number;
        status: PluploadStatus;
        lastModifiedDate: Date;
    }

    export interface UploaderConfig {
        name: string;
        url?: string;
        allowBrowse?: boolean;
        allowTypes?: {title: string; extensions: string}[];
        allowMultiSelection?: boolean;
        showInput?: boolean;
        showButtons?: boolean;
        showResult?: boolean;
        maximumOccurrences?: number;
        deferred?: boolean;
        params?: {};
    }

    export class Uploader<MODEL> extends api.dom.FormInputEl {

        private config: UploaderConfig;
        private uploader;
        private value;
        private uploadedItems: UploadItem<MODEL>[] = [];

        private input: api.ui.text.TextInput;
        private dropzone: api.dom.DivEl;

        private progress: api.ui.ProgressBar;
        private cancelBtn: Button;

        private result: api.dom.DivEl;
        private resetBtn: CloseButton;

        private uploadStartedListeners: {(event: FileUploadStartedEvent<MODEL>):void }[] = [];
        private uploadProgressListeners: {(event: FileUploadProgressEvent<MODEL>):void }[] = [];
        private fileUploadedListeners: {(event: FileUploadedEvent<MODEL>):void }[] = [];
        private uploadCompleteListeners: { (event: FileUploadCompleteEvent<MODEL>):void }[] = [];
        private uploadFailedListeners: { (event: FileUploadFailedEvent<MODEL>):void }[] = [];
        private uploadResetListeners: {():void }[] = [];

        constructor(config: UploaderConfig) {
            super("div", "file-uploader");

            this.config = config;
            // init defaults
            if (this.config.showResult == undefined) {
                this.config.showResult = true;
            }
            if (this.config.allowMultiSelection == undefined) {
                this.config.allowMultiSelection = false;
            }
            if (this.config.showButtons == undefined) {
                this.config.showButtons = true;
            }
            if (this.config.maximumOccurrences == undefined) {
                this.config.maximumOccurrences = 0;
            }
            if (this.config.allowBrowse == undefined) {
                this.config.allowBrowse = true;
            }
            if (this.config.allowTypes == undefined) {
                this.config.allowTypes = [];
            }
            if (this.config.deferred == undefined) {
                this.config.deferred = false;
            }

            if (config.showInput) {
                this.input = api.ui.text.TextInput.middle();
                this.input.setPlaceholder("Paste URL to image here");
                this.appendChild(this.input);
            }

            this.dropzone = new api.dom.DivEl("dropzone");
            // id needed for plupload to init, adding timestamp in case of multiple occurrences on page
            this.dropzone.setId('image-uploader-dropzone-' + new Date().getTime());
            this.appendChild(this.dropzone);

            this.progress = new api.ui.ProgressBar();
            this.appendChild(this.progress);

            this.result = new api.dom.DivEl('result');
            this.appendChild(this.result);

            this.cancelBtn = new Button("Cancel");
            this.cancelBtn.setVisible(this.config.showButtons);
            this.cancelBtn.onClicked((event: MouseEvent) => {
                this.stop();
                this.reset();
            });
            this.appendChild(this.cancelBtn);

            this.resetBtn = new CloseButton();
            this.resetBtn.setVisible(this.config.showButtons);
            this.resetBtn.onClicked((event: MouseEvent) => {
                this.reset();
            });
            this.appendChild(this.resetBtn);

            var resetHandler = () => {
                this.reset();
                return false;
            };
            KeyBindings.get().bindKeys([
                new KeyBinding('del', resetHandler),
                new KeyBinding('backspace', resetHandler)
            ]);

            var initHandler = (event) => {
                if (!this.uploader && this.config.url) {
                    this.uploader = this.initUploader(this.dropzone.getId());

                    if (this.value) {
                        this.setValue(this.value);
                    } else {
                        this.setDropzoneVisible();
                    }
                }
            };
            if (this.config.deferred) {
                this.onShown((event) => initHandler(event))
            } else {
                this.onRendered((event) => initHandler(event));
            }

            this.onRemoved((event) => {
                this.uploader.destroy();
            });
        }

        getName(): string {
            return this.config.name;
        }

        getValue(): string {
            return this.value;
        }

        setValue(value: string): Uploader<MODEL> {
            this.value = value;

            if (value && this.config.showResult) {
                this.setResultVisible();
            } else {
                this.setDropzoneVisible();
            }
            return this;
        }

        setMaximumOccurrences(value: number): Uploader<MODEL> {
            this.config.maximumOccurrences = value;
            return this;
        }

        stop() {
            if (this.uploader) {
                this.uploader.stop();
            }
        }

        reset() {
            this.setValue(null);
            this.notifyUploadReset();
        }

        private setDropzoneVisible(visible: boolean = true) {
            if (visible) {
                this.setProgressVisible(false);
                this.setResultVisible(false);
            }


            if (this.input) {
                this.input.setVisible(visible);
            }
            this.dropzone.setVisible(visible);
        }

        private setProgressVisible(visible: boolean = true) {
            if (visible) {
                this.setDropzoneVisible(false);
                this.setResultVisible(false);
            }

            this.progress.setVisible(visible);
            this.cancelBtn.setVisible(visible && this.config.showButtons);
        }

        private setResultVisible(visible: boolean = true) {
            if (visible) {
                this.setDropzoneVisible(false);
                this.setProgressVisible(false);
            }

            this.result.setVisible(visible);
            this.resetBtn.setVisible(visible && this.config.showButtons);
        }

        /**
         * Called on file upload finished to create model from server response
         * @param serverResponse
         */
        createModel(serverResponse): MODEL {
            throw new Error('Should be overridden by inheritors');
        }


        getModelValue(item: MODEL): string {
            throw new Error('Should be overridden by inheritors');
        }

        private findUploadItemById(id: string): UploadItem<MODEL> {
            for (var i = 0; i < this.uploadedItems.length; i++) {
                var uploadItem = this.uploadedItems[i];
                if (uploadItem.getId() == id) {
                    return uploadItem;
                }
            }
            return null;
        }

        private initUploader(elId: string) {

            if (!plupload) {
                throw new Error("FileUploader: plupload not found, check if it is included in page.");
            }

            var uploader = new plupload.Uploader({
                multipart_params: this.config.params,
                runtimes: 'gears,html5,flash,silverlight,browserplus',
                multi_selection: this.config.allowMultiSelection,
                browse_button: this.config.allowBrowse ? elId : undefined,
                url: this.config.url,
                multipart: true,
                drop_element: elId,
                flash_swf_url: api.util.UriHelper.getUri('common/js/fileupload/plupload/js/plupload.flash.swf'),
                silverlight_xap_url: api.util.UriHelper.getUri('common/js/fileupload/plupload/js/plupload.silverlight.xap'),
                filters: this.config.allowTypes
            });

            uploader.bind('Init', (up, params) => {
                console.log('uploader init', up, params);
            });

            uploader.bind('FilesAdded', (up, files: PluploadFile[]) => {
                console.log('uploader files added', up, files);

                if (this.config.maximumOccurrences > 0 && files.length > this.config.maximumOccurrences) {
                    files.splice(this.config.maximumOccurrences);
                }

                files.forEach((file: PluploadFile) => {
                    this.uploadedItems.push(new UploadItem<MODEL>(file));
                });

                this.setProgressVisible();

                // detach from the main thread
                setTimeout(function () {
                    up.start();
                }, 1);

                this.notifyFileUploadStarted(this.uploadedItems);
            });

            uploader.bind('QueueChanged', (up) => {
                console.log('uploader queue changed', up);
            });

            uploader.bind('UploadFile', (up, file) => {
                console.log('uploader upload file', up, file);
            });

            uploader.bind('UploadProgress', (up, file: PluploadFile) => {
                console.log('uploader upload progress', up, file);

                this.progress.setValue(file.percent);

                var uploadItem = this.findUploadItemById(file.id);
                if (uploadItem) {
                    uploadItem.setProgress(file.percent);
                    this.notifyFileUploadProgress(uploadItem);
                }
            });

            uploader.bind('FileUploaded', (up, file: PluploadFile, response) => {
                console.log('uploader file uploaded', up, file, response);

                if (response && response.status === 200) {
                    try {
                        var item = this.findUploadItemById(file.id);
                        if (item) {
                            var model: MODEL = this.createModel(JSON.parse(response.response));
                            item.setModel(model);
                            this.notifyFileUploaded(item);
                        }
                    } catch (e) {
                        console.warn("Failed to parse the response", response, e);
                    }
                }

            });

            uploader.bind('Error', (up, response) => {
                console.log('uploader error', up, response);

                var uploadItem = this.findUploadItemById(response.file.id);
                uploadItem.setModel(null);
                this.notifyUploadFailed(uploadItem);
            });

            uploader.bind('UploadComplete', (up, files) => {
                console.log('uploader upload complete', up, files);

                var values = [];
                this.uploadedItems.forEach((item) => {
                    if (item.getStatus() == PluploadStatus.DONE) {
                        values.push(this.getModelValue(item.getModel()));
                    }
                });

                if (values.length > 0) {
                    this.setValue(JSON.stringify(values));
                    this.notifyUploadCompleted(this.uploadedItems);
                }

                this.uploadedItems.length = 0;
                this.uploader.splice();
            });

            uploader.init();

            return uploader;
        }

        getResultContainer(): api.dom.DivEl {
            return this.result;
        }

        onUploadStarted(listener: (event: FileUploadStartedEvent<MODEL>) => void) {
            this.uploadStartedListeners.push(listener);
        }

        unUploadStarted(listener: (event: FileUploadStartedEvent<MODEL>) => void) {
            this.uploadStartedListeners = this.uploadStartedListeners.filter((currentListener) => {
                return listener != currentListener;
            });
        }

        onUploadProgress(listener: (event: FileUploadProgressEvent<MODEL>) => void) {
            this.uploadProgressListeners.push(listener);
        }

        unUploadProgress(listener: (event: FileUploadProgressEvent<MODEL>) => void) {
            this.uploadProgressListeners = this.uploadProgressListeners.filter((currentListener) => {
                return listener != currentListener;
            });
        }

        onFileUploaded(listener: (event: FileUploadedEvent<MODEL>) => void) {
            this.fileUploadedListeners.push(listener);
        }

        unFileUploaded(listener: (event: FileUploadedEvent<MODEL>) => void) {
            this.fileUploadedListeners = this.fileUploadedListeners.filter((currentListener) => {
                return listener != currentListener;
            })
        }

        onUploadCompleted(listener: (event: FileUploadCompleteEvent<MODEL>) => void) {
            this.uploadCompleteListeners.push(listener);
        }

        unUploadCompleted(listener: (event: FileUploadCompleteEvent<MODEL>) => void) {
            this.uploadCompleteListeners = this.uploadCompleteListeners.filter((currentListener) => {
                return listener != currentListener;
            });
        }

        onUploadReset(listener: () => void) {
            this.uploadResetListeners.push(listener);
        }

        unUploadReset(listener: () => void) {
            this.uploadResetListeners = this.uploadResetListeners.filter((currentListener) => {
                return listener != currentListener;
            })
        }

        onUploadFailed(listener: (event: FileUploadFailedEvent<MODEL>) => void) {
            this.uploadFailedListeners.push(listener);
        }

        unUploadFailed(listener: (event: FileUploadFailedEvent<MODEL>) => void) {
            this.uploadFailedListeners = this.uploadFailedListeners.filter((currentListener) => {
                return listener != currentListener;
            });
        }

        private notifyFileUploadStarted(uploadItems: UploadItem<MODEL>[]) {
            this.uploadStartedListeners.forEach((listener: (event: FileUploadStartedEvent<MODEL>)=>void) => {
                listener(new FileUploadStartedEvent<MODEL>(uploadItems));
            });
        }

        private notifyFileUploadProgress(uploadItem: UploadItem<MODEL>) {
            this.uploadProgressListeners.forEach((listener: (event: FileUploadProgressEvent<MODEL>)=>void) => {
                listener(new FileUploadProgressEvent<MODEL>(uploadItem));
            });
        }

        private notifyFileUploaded(uploadItem: UploadItem<MODEL>) {
            this.fileUploadedListeners.forEach((listener: (event: FileUploadedEvent<MODEL>)=>void) => {
                listener.call(this, new FileUploadedEvent<MODEL>(uploadItem));
            });
        }

        private notifyUploadCompleted(uploadItems: UploadItem<MODEL>[]) {
            this.uploadCompleteListeners.forEach((listener: (event: FileUploadCompleteEvent<MODEL>)=>void) => {
                listener(new FileUploadCompleteEvent<MODEL>(uploadItems));
            });
        }

        private notifyUploadReset() {
            this.uploadResetListeners.forEach((listener: ()=>void) => {
                listener.call(this);
            });
        }

        private notifyUploadFailed(uploadItem: UploadItem<MODEL>) {
            this.uploadFailedListeners.forEach((listener: (event: FileUploadFailedEvent<MODEL>)=>void) => {
                listener(new FileUploadFailedEvent<MODEL>(uploadItem));
            });
        }
    }
}