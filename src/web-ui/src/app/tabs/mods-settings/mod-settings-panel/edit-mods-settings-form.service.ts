import { Injectable } from "@angular/core";
import { AbstractControl, FormBuilder, FormGroup, Validators } from "@angular/forms";
import { ModSettings } from "../../../model/mod-settings.model";

@Injectable({
  providedIn: "root"
})
export class EditModsSettingsFormService {
  constructor(private readonly formBuilder: FormBuilder) {}

  getForm(): FormGroup {
    return this.formBuilder.group({
      id: [null],
      name: ["", Validators.required],
      active: [true, Validators.required],
      content: [null]
    });
  }

  setForm(form: FormGroup, data: ModSettings, content: string | undefined) {
    this.getIdControl(form).setValue(data.id);
    this.getNameControl(form).setValue(data.name);
    this.getActiveControl(form).setValue(data.active);
    this.setContentControl(form, content);
  }

  asModSettings(form: FormGroup): ModSettings {
    return {
      id: this.getIdControl(form).value,
      name: this.getNameControl(form).value,
      active: this.getActiveControl(form).value,
      content: this.getContentControl(form).value
    } as ModSettings;
  }

  setContentControl(form: FormGroup, content: string | undefined) {
    this.getContentControl(form).setValue(content);
  }

  getIdControl(form: FormGroup) {
    return form.get("id") as AbstractControl<number | undefined>;
  }

  getNameControl(form: FormGroup) {
    return form.get("name") as AbstractControl<string>;
  }

  getActiveControl(form: FormGroup) {
    return form.get("active") as AbstractControl<boolean>;
  }

  getContentControl(form: FormGroup) {
    return form.get("content") as AbstractControl<string | undefined>;
  }
}
