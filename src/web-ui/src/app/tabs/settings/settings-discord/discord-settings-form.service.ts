import { inject, Injectable } from "@angular/core";
import { AbstractControl, FormBuilder, FormGroup, Validators } from "@angular/forms";
import { DiscordIntegrationSettings } from "../../../model/discord-settings.model";

@Injectable({
  providedIn: "root"
})
export class DiscordSettingsFormService {
  private readonly formBuilder: FormBuilder = inject(FormBuilder);

  getForm(): FormGroup {
    return this.formBuilder.group({
      enabled: [false, [Validators.required]],
      webhookUrl: [""],
      serverStartingMessage: [""],
      serverStartMessage: [""],
      serverStopMessage: [""],
      serverUpdateMessage: [""]
    });
  }

  setForm(form: FormGroup, settings: DiscordIntegrationSettings) {
    this.getEnabledControl(form).setValue(settings.enabled);
    this.getWebhookUrlControl(form).setValue(settings.webhookUrl);
    this.getServerStartingMessageControl(form).setValue(settings.serverStartingMessage);
    this.getServerStartMessageControl(form).setValue(settings.serverStartMessage);
    this.getServerStopMessageControl(form).setValue(settings.serverStopMessage);
    this.getServerUpdateMessageControl(form).setValue(settings.serverUpdateMessage);
  }

  asSettings(form: FormGroup): DiscordIntegrationSettings {
    return {
      enabled: this.getEnabledControl(form).value,
      webhookUrl: this.getWebhookUrlControl(form).value,
      serverStartingMessage: this.getServerStartingMessageControl(form).value,
      serverStartMessage: this.getServerStartMessageControl(form).value,
      serverStopMessage: this.getServerStopMessageControl(form).value,
      serverUpdateMessage: this.getServerUpdateMessageControl(form).value
    } as DiscordIntegrationSettings;
  }

  getEnabledControl(form: FormGroup) {
    return form.get("enabled") as AbstractControl<boolean>;
  }

  getWebhookUrlControl(form: FormGroup) {
    return form.get("webhookUrl") as AbstractControl<string>;
  }

  getServerStartingMessageControl(form: FormGroup) {
    return form.get("serverStartingMessage") as AbstractControl<string>;
  }

  getServerStartMessageControl(form: FormGroup) {
    return form.get("serverStartMessage") as AbstractControl<string>;
  }

  getServerUpdateMessageControl(form: FormGroup) {
    return form.get("serverUpdateMessage") as AbstractControl<string>;
  }

  getServerStopMessageControl(form: FormGroup) {
    return form.get("serverStopMessage") as AbstractControl<string>;
  }
}
