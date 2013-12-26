module app_home {

    export class Branding extends api_dom.DivEl {

        private installation:InstallationInfo;
        private version:VersionInfo;

        constructor(installation?:string, version?:string) {
            super(null, 'branding');

            installation = installation || '';
            version = version || '';

            this.installation = new InstallationInfo(installation);
            this.version = new VersionInfo(version);
            this.appendChild(this.installation);
            this.appendChild(this.version);

            api_remote_util.RemoteSystemService.system_getSystemInfo({}, (result: api_remote_util.SystemGetSystemInfoResult) => {
                this.setInstallation(result.installationName);
                this.setVersion(result.version);
            });
        }

        getInstallation():string {
            return this.installation.getInstallation();
        }

        setInstallation(installationText:string):void {
            return this.installation.setInstallation(installationText);
        }

        getVersion():string {
            return this.version.getVersion();
        }

        setVersion(versionText:string):void {
            return this.version.setVersion(versionText);
        }
    }

}
