import {Component} from '@angular/core';
import {ModSettingsService} from "../../../service/mod-settings.service";
import {FormGroup} from "@angular/forms";
import {EditModsSettingsFormService} from "./edit-mods-settings-form.service";
import {ActivatedRoute} from "@angular/router";
import {MaskService} from "../../../service/mask.service";

@Component({
  selector: 'app-edit-mod-settings',
  templateUrl: './edit-mod-settings.component.html',
  styleUrl: './edit-mod-settings.component.css'
})
export class EditModSettingsComponent {

  form: FormGroup;

  constructor(private modSettingsService: ModSettingsService,
              private formService: EditModsSettingsFormService,
              private activatedRoute: ActivatedRoute,
              private maskService: MaskService) {
    this.maskService.show();
    this.form = formService.getForm();
    this.modSettingsService.getModSettings(this.activatedRoute.snapshot.params['id']).subscribe(response => {
      this.formService.setForm(this.form, response, '');
      this.modSettingsService.getModSettingsContent(this.formService.getIdControl(this.form).value).subscribe(response => {
        this.formService.setContentControl(this.form, response.content);
        this.maskService.hide();
      });
    });
  }

  save() {
    this.form.markAllAsTouched();
    if (this.form.valid) {
      this.maskService.show();
      this.modSettingsService.updateModSettings(this.getId(), this.formService.asModSettings(this.form)).subscribe(response => {
        this.modSettingsService.saveModSettingsContent(this.getId(), this.getContent()).subscribe(response => {
          this.maskService.hide();
        });
      });
    }
  }

  getId() {
    return this.formService.getIdControl(this.form).value;
  }

  getName() {
    return this.formService.getNameControl(this.form).value;
  }

  getActive() {
    return this.formService.getActiveControl(this.form).value;
  }

  getContent() {
    return this.formService.getContentControl(this.form).value;
  }
}
