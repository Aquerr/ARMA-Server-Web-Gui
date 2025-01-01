import {inject, Injectable} from "@angular/core";
import {AbstractControl, FormBuilder, FormGroup, Validators} from "@angular/forms";

@Injectable({
  providedIn: 'root',
})
export class DiscordSettingsFormService {
  private readonly formBuilder: FormBuilder = inject(FormBuilder);

  getForm(): FormGroup {
    return this.formBuilder.group({
      enabled: [false, [Validators.required]],
      serverStartMessage: [''],
      serverStopMessage: [''],
      serverUpdateMessage: ['']
    });
  }

  setForm(form: FormGroup, settings: DiscordIntegrationSettings) {
    this.getEnabledControl(form).setValue(settings.enabled);
    this.getServerStartMessageControl(form).setValue(settings.serverStartMessage);
    this.getServerStopMessageControl(form).setValue(settings.serverStopMessage);
    this.getServerUpdateMessageControl(form).setValue(settings.serverUpdateMessage);
  }

  asAswgUser(form: FormGroup): DiscordIntegrationSettings {
    return {
      enabled: this.getEnabledControl(form).value,
      serverStartMessage: this.getServerStartMessageControl(form).value,
      serverStopMessage: this.getServerStopMessageControl(form).value,
      serverUpdateMessage: this.getServerUpdateMessageControl(form).value,
    } as DiscordIntegrationSettings;
  }

  getEnabledControl(form: FormGroup): AbstractControl {
    return form.get('enabled') as AbstractControl;
  }

  getServerStartMessageControl(form: FormGroup): AbstractControl {
    return form.get('serverStartMessage') as AbstractControl;
  }

  getServerUpdateMessageControl(form: FormGroup): AbstractControl {
    return form.get('serverUpdateMessage') as AbstractControl;
  }

  getServerStopMessageControl(form: FormGroup): AbstractControl {
    return form.get('serverStopMessage') as AbstractControl;
  }
}

export interface DiscordIntegrationSettings {
  enabled: boolean;
  serverStartMessage: string;
  serverStopMessage: string;
  serverUpdateMessage: string;
}
