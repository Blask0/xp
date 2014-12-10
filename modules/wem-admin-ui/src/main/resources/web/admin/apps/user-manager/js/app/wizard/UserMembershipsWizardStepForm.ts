module app.wizard {

    import User = api.security.User;
    import Principal = api.security.Principal;
    import PrincipalKey = api.security.PrincipalKey;
    import PrincipalType = api.security.PrincipalType;
    import PrincipalLoader = api.security.PrincipalLoader;

    import PrincipalComboBox = api.ui.security.PrincipalComboBox;

    import DivEl = api.dom.DivEl;
    import LabelEl = api.dom.LabelEl;

    export class UserMembershipsWizardStepForm extends api.app.wizard.WizardStepForm {

        private groups: PrincipalComboBox;

        private roles: PrincipalComboBox;

        private labelGroups: LabelEl;

        private labelRoles: LabelEl;

        private principal: Principal;

        private groupsLoaded: boolean;

        private rolesLoaded: boolean;

        constructor() {
            super("user-memberships");

            this.groupsLoaded = false;
            this.rolesLoaded = false;

            var groupsLoader = new PrincipalLoader().setAllowedTypes([PrincipalType.GROUP]);
            var rolesLoader = new PrincipalLoader().setAllowedTypes([PrincipalType.ROLE]);

            this.groups = new PrincipalComboBox(groupsLoader);
            this.roles = new PrincipalComboBox(rolesLoader);

            var groupsHandler = () => { this.groupsLoaded = true; this.selectMembership(); this.groups.unLoaded(groupsHandler); };
            var rolesHandler = () => { this.rolesLoaded = true; this.selectMembership(); this.roles.unLoaded(rolesHandler); };

            this.groups.onLoaded(groupsHandler);
            this.roles.onLoaded(rolesHandler);

            this.labelGroups = new LabelEl("Groups", this.groups, "input-label");
            this.labelRoles = new LabelEl("Roles", this.roles, "input-label");

            var formViewGroups = new DivEl("form-view user-groups-memberships"),
                formViewRoles = new DivEl("form-view user-roles-memberships"),
                inputViewGroups = new DivEl("input-view valid"),
                inputViewRoles = new DivEl("input-view valid"),
                inputTypeViewGroups = new DivEl("input-type-view"),
                inputTypeViewRoles = new DivEl("input-type-view"),
                inputOccurrenceViewGroups = new DivEl("input-occurrence-view single-occurrence"),
                inputOccurrenceViewRoles = new DivEl("input-occurrence-view single-occurrence"),
                inputWrapperGroups = new DivEl("input-wrapper"),
                inputWrapperRoles = new DivEl("input-wrapper");

            inputWrapperGroups.appendChild(this.groups);
            inputOccurrenceViewGroups.appendChild(inputWrapperGroups);
            inputTypeViewGroups.appendChild(inputOccurrenceViewGroups);
            inputViewGroups.appendChild(this.labelGroups);
            inputViewGroups.appendChild(inputTypeViewGroups);
            formViewGroups.appendChild(inputViewGroups);

            inputWrapperRoles.appendChild(this.roles);
            inputOccurrenceViewRoles.appendChild(inputWrapperRoles);
            inputTypeViewRoles.appendChild(inputOccurrenceViewRoles);
            inputViewRoles.appendChild(this.labelRoles);
            inputViewRoles.appendChild(inputTypeViewRoles);
            formViewRoles.appendChild(inputViewRoles);

            this.appendChild(formViewGroups);
            this.appendChild(formViewRoles);
        }

        layout(principal: Principal) {
            this.principal = principal;
            this.selectMembership();
        }

        private selectMembership(): void {
            if (!!this.principal && this.groupsLoaded && this.rolesLoaded) {

                var groups = this.principal.asUser().getMemberships().
                    filter((el) => { return el.isGroup()}).
                    map((el) => { return el.getKey().getId(); });

                var roles = this.principal.asUser().getMemberships().
                    filter((el) => { return el.isRole()}).
                    map((el) => { return el.getKey().getId(); });

                this.groups.getDisplayValues().filter((principal: Principal) => {
                    return groups.indexOf(principal.getKey().getId()) >= 0;
                }).forEach((selection) => {
                    this.groups.select(selection);
                });

                this.roles.getDisplayValues().filter((principal: Principal) => {
                    return roles.indexOf(principal.getKey().getId()) >= 0;
                }).forEach((selection) => {
                    this.roles.select(selection);
                });
            }
        }

        getMemberships(): Principal[] {
            return this.groups.getSelectedDisplayValues().
                concat(this.roles.getSelectedDisplayValues()).
                map((el) => { return Principal.fromPrincipal(el); });
        }

        giveFocus(): boolean {
            return this.groups.giveFocus();
        }
    }
}
