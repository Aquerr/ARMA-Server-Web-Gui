import { inject, Injectable } from "@angular/core";
import { AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, Validators } from "@angular/forms";
import { JobSettings } from "../../../../model/job-settings.model";

@Injectable({
  providedIn: "root"
})
export class JobSettingsFormService {
  private readonly formBuilder: FormBuilder = inject(FormBuilder);

  getForm(): FormGroup {
    return this.formBuilder.group({
      enabled: [""],
      cron: [""],
      parameters: this.formBuilder.array([
        this.formBuilder.group({
          name: new FormControl({value: "", disabled: true}),
          description: new FormControl({value: "", disabled: true}),
          value: ["", Validators.required],
        })]),
      lastExecutionDate: new FormControl({value: "", disabled: true}),
      lastExecutionFinishedDate: new FormControl({value: "", disabled: true}),
      nextExecutionDate: new FormControl({value: "", disabled: true}),
      lastMessage: [""],
      lastStatus: [""]
    });
  }

  setForm(form: FormGroup, settings: JobSettings) {
    this.getEnabledControl(form).setValue(settings.enabled);
    this.getCronControl(form).setValue(settings.cron);
    this.getLastExecutionDateControl(form).setValue(settings.lastExecutionDate);
    this.getLastExecutionFinishedDateControl(form).setValue(settings.lastExecutionFinishedDate);
    this.getNextExecutionDateControl(form).setValue(settings.nextExecutionDate);
    this.getLastMessage(form).setValue(settings.lastMessage);
    this.getLastStatus(form).setValue(settings.lastStatus);

    this.getParametersControl(form).clear();
    settings.parameters.forEach(parameter => this.getParametersControl(form)
      .push(this.formBuilder.group({
        name: [parameter.name],
        description: new FormControl({value: parameter.description, disabled: true}),
        value: [parameter.value, Validators.required],
      })));
  }

  asSettings(form: FormGroup): JobSettings {
    return {
      enabled: this.getEnabledControl(form).value,
      cron: this.getCronControl(form).value,
      parameters: this.getParametersControl(form).value
    } as JobSettings;
  }

  getEnabledControl(form: FormGroup): AbstractControl {
    return form.get("enabled") as AbstractControl;
  }

  getCronControl(form: FormGroup): AbstractControl {
    return form.get("cron") as AbstractControl;
  }

  getParametersControl(form: FormGroup): FormArray {
    return form.get("parameters") as FormArray;
  }

  getLastExecutionDateControl(form: FormGroup): AbstractControl {
    return form.get("lastExecutionDate") as AbstractControl;
  }

  getLastExecutionFinishedDateControl(form: FormGroup): AbstractControl {
    return form.get("lastExecutionFinishedDate") as AbstractControl;
  }

  getNextExecutionDateControl(form: FormGroup): AbstractControl {
    return form.get("nextExecutionDate") as AbstractControl;
  }

  getLastMessage(form: FormGroup): AbstractControl {
    return form.get('lastMessage') as AbstractControl;
  }

  getLastStatus(form: FormGroup): AbstractControl {
    return form.get('lastStatus') as AbstractControl;
  }
}
