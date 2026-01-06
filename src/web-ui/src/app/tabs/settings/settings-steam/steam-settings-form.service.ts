import { inject, Injectable } from "@angular/core";
import { AbstractControl, FormBuilder, FormGroup } from "@angular/forms";
import { SteamSettings } from "src/app/model/steam-settings.model";

@Injectable({
  providedIn: "root"
})
export class SteamSettingsFormService {
  private readonly formBuilder: FormBuilder = inject(FormBuilder);

  getForm(): FormGroup {
    return this.formBuilder.group({
      steamCmdPath: [""],
      steamCmdUsername: [""],
      steamCmdPassword: [""],
      steamCmdWorkshopContentPath: [""],
      steamWebApiToken: [""]
    });
  }

  setForm(form: FormGroup, settings: SteamSettings) {
    this.getSteamCmdPathControl(form).setValue(settings.steamCmdPath);
    this.getSteamCmdUsernameControl(form).setValue(settings.steamCmdUsername);
    this.getSteamCmdPasswordControl(form).setValue(settings.steamCmdPassword);
    this.getSteamCmdWorkshopContentPathControl(form).setValue(settings.steamCmdWorkshopContentPath);
    this.getSteamWebApiTokenControl(form).setValue(settings.steamWebApiToken);
  }

  asSettings(form: FormGroup): SteamSettings {
    return {
      steamCmdPath: this.getSteamCmdPathControl(form).value,
      steamCmdUsername: this.getSteamCmdUsernameControl(form).value,
      steamCmdPassword: this.getSteamCmdPasswordControl(form).value,
      steamCmdWorkshopContentPath: this.getSteamCmdWorkshopContentPathControl(form).value,
      steamWebApiToken: this.getSteamWebApiTokenControl(form).value
    } as SteamSettings;
  }

  getSteamCmdPathControl(form: FormGroup): AbstractControl<string> {
    return form.get("steamCmdPath") as AbstractControl<string>;
  }

  getSteamCmdUsernameControl(form: FormGroup): AbstractControl<string> {
    return form.get("steamCmdUsername") as AbstractControl<string>;
  }

  getSteamCmdPasswordControl(form: FormGroup): AbstractControl<string> {
    return form.get("steamCmdPassword") as AbstractControl<string>;
  }

  getSteamCmdWorkshopContentPathControl(form: FormGroup): AbstractControl<string> {
    return form.get("steamCmdWorkshopContentPath") as AbstractControl<string>;
  }

  getSteamWebApiTokenControl(form: FormGroup): AbstractControl<string> {
    return form.get("steamWebApiToken") as AbstractControl<string>;
  }
}
