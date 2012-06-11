var g_summaryData = {
    "expanded": true,
    "children": [
        {
            "label": "Display Name",
            "fieldType": "String", // For account user could we use existing map to compute type?
            "newValue": "Thomas Sigdestad",
            "previousValue": "Tomas Lund Sig",
            "changeType": "modified",
            "leaf": true
        },
        {
            "label":"1. Profile",
            "expanded":true,
            "children": [
                {
                    "label":"First Name",
                    "fieldType": "String",
                    "newValue":"Thomas",
                    "previousValue": "Tomas",
                    "changeType": "modified",
                    "leaf":true
                },
                {
                    "label":"Middle Name",
                    "fieldType": "String",
                    "newValue":"",
                    "previousValue": "Lund",
                    "changeType": "removed",
                    "leaf":true
                },
                {
                    "label":"Last Name",
                    "fieldType": "String",
                    "newValue":"Sigdestad",
                    "previousValue": "Sig",
                    "changeType": "modified",
                    "leaf":true
                },
                {
                    "label":"Organization",
                    "fieldType": "String",
                    "newValue":"Enonic",
                    "previousValue": "Nordicom",
                    "changeType": "modified",
                    "leaf":true
                }
            ]
        },
        {
            "label": "2. User",
            "expanded": true,
            "children": [
                {
                    "label": "E-mail",
                    "fieldType": "String",
                    "newValue": "tsi@enonic.com",
                    "previousValue": "tsi@enonic.com",
                    "changeType": "none",
                    "leaf": true
                },
                {
                    "label": "Password",
                    "fieldType": "Password",
                    "newValue": "*********",
                    "previousValue": "********",
                    "changeType": "modified",
                    "leaf": true
                }
            ]
        },
        {
            "label": "3. Places",
            "expanded": true,
            "children": [
                {
                    "label": "Address",
                    "fieldType": "BlockGroup",
                    "expanded": true,
                    // Is modified if one or more children is modified
                    "newValue": "[1]",
                    "previousValue": "[2]",
                    "changeType": "modified",
                    "children": [
                        {
                            "label": "Label",
                            "fieldType": "String",
                            "newValue": "Home",
                            "previousValue": "",
                            "changeType": "added",
                            "leaf": true
                        }
                    ]
                },
                {
                    "label": "Address",
                    "fieldType": "BlockGroup",
                    "expanded": true,
                    "newValue": "[2]",
                    "previousValue": "[1]",
                    "changeType": "modified",
                    "children": [
                        {
                            "label": "Region",
                            "fieldType": "String",
                            "newValue": "",
                            "previousValue": "&Oslash;stfold",
                            "changeType": "removed",
                            "leaf": true
                        }
                    ]
                }

            ]
        },
        {
            "label": "4. Memberships",
            "expanded": true,
            "children": [
                {
                    "label": "Member Of",
                    "fieldType": "RelatedList",
                    "newValue": "3 Accounts (2 added, 1 removed)",
                    "previousValue": "3 Accounts (2 added, 1 removed)",
                    "changeType": "modified",
                    "leaf": true
                }
            ]
        }
    ]
};