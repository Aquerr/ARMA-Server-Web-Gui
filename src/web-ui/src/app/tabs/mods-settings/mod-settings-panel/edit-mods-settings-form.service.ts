import { Injectable } from "@angular/core";
import { FormBuilder, FormControl, FormGroup, Validators } from "@angular/forms";
import { ModSettings } from "@model/mod-settings.model";

export interface EditModSettingsFormControls {
  id: FormControl<number | undefined | null>;
  name: FormControl<string>;
  active: FormControl<boolean>;
  content: FormControl<string | undefined | null>;
}

@Injectable({
  providedIn: "root"
})
export class EditModsSettingsFormService {
  constructor(private readonly formBuilder: FormBuilder) {}

  getForm(): FormGroup<EditModSettingsFormControls> {
    return this.formBuilder.group<EditModSettingsFormControls>({
      id: this.formBuilder.control(null),
      name: this.formBuilder.nonNullable.control("", [Validators.required]),
      active: this.formBuilder.nonNullable.control(true, [Validators.required]),
      content: this.formBuilder.control(null)
    });
  }

  setForm(form: FormGroup<EditModSettingsFormControls>, data: ModSettings, content: string | undefined) {
    form.patchValue({
      id: data.id,
      name: data.name,
      active: data.active,
      content: content
    });
  }

  asModSettings(form: FormGroup<EditModSettingsFormControls>): ModSettings {
    const value = form.value;
    return {
      id: value.id,
      name: value.name,
      active: value.active,
      content: value.content
    } as ModSettings;
  }
}
