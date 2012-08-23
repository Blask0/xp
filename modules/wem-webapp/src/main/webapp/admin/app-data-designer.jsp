<%@ taglib prefix="w" uri="uri:enonic.wem.taglib" %>
<!DOCTYPE html>
<w:helper var="helper"/>
<html>
<head>
  <meta charset="utf-8"/>
  <title>Enonic WEM Admin</title>
  <link rel="stylesheet" type="text/css" href="resources/lib/ext/resources/css/ext-all.css">
  <link rel="stylesheet" type="text/css" href="resources/css/main.css">
  <link rel="stylesheet" type="text/css" href="resources/css/icons.css">
  <link rel="stylesheet" type="text/css" href="resources/css/datadesigner-preview-panel.css">
  <link rel="stylesheet" type="text/css" href="resources/css/admin-tree-panel.css">

  <!-- ExtJS -->

  <script type="text/javascript" src="resources/lib/ext/ext-all-debug.js"></script>

  <!-- Configuration -->

  <script type="text/javascript" src="global.config.js"></script>
  <script type="text/javascript">

    window.CONFIG = {
      baseUrl: '<%= helper.getBaseUrl().substring( 0, helper.getBaseUrl().lastIndexOf( "/admin" ) )%>'
    };

    Ext.Loader.setConfig({
      paths: {
        'Common': 'common/js',
        'Main': 'app/main/js',
        'Admin': 'resources/app'
      }
    });
  </script>

  <!-- Templates -->

  <script type="text/javascript" src="resources/app/view/XTemplates.js"></script>

  <!-- Third party plugins -->

  <script type="text/javascript" src="resources/lib/plupload/js/plupload.full.js"></script>

  <!-- Application -->

  <script type="text/javascript">
    Ext.application({
      name: 'App',

      controllers: [
        'Admin.controller.datadesigner.BrowseController',
        'Admin.controller.datadesigner.FilterPanelController'
      ],

      requires: [
        'Admin.view.TabPanel',
        'Admin.lib.UriHelper'
      ],

      launch: function () {
        Ext.create('Ext.container.Viewport', {
          region: 'center',
          title: 'Browse',
          layout: 'border',
          padding: 0,
          items: [
            {
              region: 'center',
              xtype: 'cmsTabPanel',
              bodyCls: 'admin-no-border',
              items: [
                {
                  title: 'Browse',
                  closable: false,
                  layout: 'border',
                  border: false,
                  items: [
                    {
                      region: 'west',
                      xtype: 'datadesignerFilter',
                      width: 182,
                      minSize: 100,
                      maxSize: 500,
                      margins: '3 0 5 5'

                    },
                    {
                      region: 'center',
                      layout: 'border',
                      border: false,
                      margins: '3 5 5 0',
                      items: [
                        {
                          region: 'center',
                          xtype: 'contentTypeTreeGridPanel',
                          flex: 2
                        },
                        {
                          region: 'south',
                          xtype: 'contentTypeDetailPanel',
                          collapsible: true,
                          border: true,
                          split: true,
                          flex: 1
                        }
                      ]
                    }
                  ]
                }
              ]
            }
          ]
        });
      }
    });
  </script>

</head>
<body>
</body>
</html>
