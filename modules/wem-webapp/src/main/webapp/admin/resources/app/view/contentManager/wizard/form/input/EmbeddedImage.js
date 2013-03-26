Ext.define('Admin.view.contentManager.wizard.form.input.EmbeddedImage', {
    extend: 'Admin.view.contentManager.wizard.form.input.Base',
    alias: 'widget.EmbeddedImage',
    fieldLabel: 'EmbeddedImage',
    requires: [
        'Admin.store.contentManager.ContentStore'
    ],

    initComponent: function () {

        this.selectedContentStore = this.createSelectedContentStore();

        this.items = [
            this.createHiddenInput(),
            this.createComboBox(),
            this.createUploadButton(),
            this.createViewForSelectedContent()
        ];

        this.callParent(arguments);
    },


    getValue: function () {
        var value = this.items.items[0].getValue();
        if (value && Ext.isString(value)) {
            value = value.split(',');
        } else {
            return null;
        }

        var valueList = [];
        for (var i = 0; i < value.length; i++) {
            var currentItemValue = {
                'path': this.name.concat('[', i, ']'),
                'value': value[i]
            };
            valueList.push(currentItemValue);
        }
        return valueList;
    },

    setValue: function (value) {
        console.log(value);
    },

    /**
     * @private
     */
    createHiddenInput: function () {
        return {
            xtype: 'hiddenfield',
            name: this.name,
            itemId: this.name, // TODO: Is this unique enough?
            value: ''
        };
    },


    /**
     * @private
     */
    createComboBox: function () {
        var me = this;

        var template = new Ext.XTemplate(
            '<tpl for=".">',
            '   <div class="x-boundlist-item">',
            '       <div>',
            '           <img src="{iconUrl}?size=32" alt="{displayName}" style="float:left; padding-right: 5px;"/>',
            '           <div>',
            '               <h5>{displayName}</h5>',
            '               <div style="color: #666">{path}</div>',
            '           </div>',
            '       </div>',
            '   </div>',
            '</tpl>'
        );

        var combo = {
            xtype: 'combo',
            emptyText: 'Start typing',
            submitValue: false,
            hideTrigger: true,
            forceSelection: true,
            minChars: 1,
            queryMode: 'remote',
            queryParam: 'fulltext',
            autoSelect: false,

            displayField: 'displayName',
            valueField: 'id',
            tpl: template,

            store: new Admin.store.contentManager.ContentStore(),
            listeners: {
                select: function (combo, records) {
                    combo.setValue('');
                    me.onSelectContent(records);
                }
            }
        };

        return combo;
    },


    /**
     * @private
     */
    onSelectContent: function (contentModels) {
        var contentModel = contentModels[0];
        var isAlreadyAdded = this.selectedContentStore.findRecord('id', contentModel.getId());
        if (isAlreadyAdded) {
            this.alertContentIsAdded(contentModel);
            return;
        }
        this.selectedContentStore.add(contentModel);
    },


    /**
     * @private
     */
    createSelectedContentStore: function () {
        var me = this;

        return Ext.create('Ext.data.Store', {
            model: 'Admin.model.contentManager.ContentModel',
            data: [],
            listeners: {
                datachanged: function (store) {
                    me.updateHiddenValue();
                    try {
                        me.down('combobox').setDisabled(me.selectedContentStore.getCount() ===
                                                        me.contentTypeItemConfig.occurrences.maximum);
                    }
                    catch (exception) {
                        /**/
                    }
                }
            }
        });
    },


    /**
     * @private
     */
    createViewForSelectedContent: function () {
        var me = this;

        var template = new Ext.XTemplate(
            '<tpl for=".">',
            '   <div class="admin-related-item" style="padding-top:3px">',
            '       <img src="{iconUrl}" alt="{displayName}"/>',
            '       <div class="center-column">',
            '           <h6>{displayName}</h6>',
            '       </div>',
            '       <div class="right-column"><a href="javascript:;" class="remove-related-item-button">Remove</a></div>',
            '   </div>',
            '</tpl>'
        );

        return Ext.create('Ext.view.View', {
            store: me.selectedContentStore,
            tpl: template,
            itemSelector: 'div.admin-related-item',
            emptyText: 'No items selected',
            deferEmptyText: false,
            listeners: {
                itemclick: function (view, contentModel, item, index, e) {
                    var clickedElement = Ext.fly(e.target);
                    if (clickedElement.hasCls('remove-related-item-button')) {
                        me.selectedContentStore.remove(contentModel);
                    }
                }
            }
        });
    },

    /**
     * @private
     */
    createUploadButton: function () {
        var uploadButton = {
            xtype: 'button',
            itemId: 'uploadButton',
            text: 'Upload image',
            maxWidth: 150,
            handler: function () {
                console.log('TODO: show image uploader');
            }
        };
        return uploadButton;
    },

    /**
     * @private
     */
    alertContentIsAdded: function (contentModel) {
        console.log('Temporary alert! Can not have duplicates in Image input\n"' + contentModel.get('path') + '" has already been added');
        this.down('combobox').focus('');
    },


    /**
     * @private
     */
    updateHiddenValue: function () {
        var me = this;
        var ids = [];
        if (this.items) {
            Ext.Array.each(me.selectedContentStore.data.items, function (item) {
                ids.push(item.data.id);
            });
            this.getComponent(this.name).setValue(ids);
        }
    }

});