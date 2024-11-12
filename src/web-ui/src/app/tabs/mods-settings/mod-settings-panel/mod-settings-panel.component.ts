import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {ModSettings} from "../../../model/mod-settings.model";
import {ModSettingsService} from "../../../service/mod-settings.service";
import {FormGroup} from "@angular/forms";
import {EditModsSettingsFormService} from "./edit-mods-settings-form.service";
import {MaskService} from "../../../service/mask.service";
import {CodeJarContainer} from "ngx-codejar";
import hljs from 'highlight.js';

@Component({
  selector: 'app-mod-settings-panel',
  templateUrl: './mod-settings-panel.component.html',
  styleUrl: './mod-settings-panel.component.scss'
})
export class ModSettingsPanelComponent implements OnInit, OnChanges {

  @Input({required: true}) modSettings!: ModSettings;
  @Output("deleted") modSettingsDeleted = new EventEmitter<number>();
  @Output("activated") modSettingsActivated = new EventEmitter<ModSettings>();

  public form!: FormGroup;

  constructor(private modSettingsService: ModSettingsService,
              public formService: EditModsSettingsFormService,
              private maskService: MaskService) {

  }

  ngOnInit() {
    this.form = this.formService.getForm();
    this.formService.setForm(this.form, this.modSettings, undefined);
  }

  onOpen() {
    if (!this.getContent() && this.modSettings.id) {
      this.maskService.show();
      this.modSettingsService.getModSettingsContent(this.modSettings.id).subscribe(response => {
        this.formService.setContentControl(this.form, response.content);
        this.maskService.hide();
      });
    }
  }

  save() {
    this.form.markAllAsTouched();
    if (this.form.valid) {
      this.maskService.show();
      if (this.getId()) {
        this.modSettingsService.updateModSettings(this.getId(), this.formService.asModSettings(this.form)).subscribe(response => {
          this.maskService.hide();
        });
      } else {
        this.modSettingsService.createNewModSettings(this.formService.asModSettings(this.form)).subscribe(response => {
          this.modSettings = {
            ...this.modSettings,
            id: response.id
          }
          this.maskService.hide();
        });
      }
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

  deleteClick(event: MouseEvent) {
    event.stopPropagation();
    this.modSettingsDeleted.emit(this.getId());
  }

  toggleActive(event: MouseEvent) {
    event.stopPropagation();
    this.formService.getActiveControl(this.form).setValue(!this.getActive());
    this.modSettingsActivated.emit(this.formService.asModSettings(this.form));
  }

  highlightMethod(editor: CodeJarContainer) {
    if (editor.textContent !== null && editor.textContent !== undefined) {
      editor.innerHTML = hljs.highlight(editor.textContent, {
        language: 'sql'
      }).value;
    }
  }

  onCodeChange(code: string) {
    this.formService.setContentControl(this.form, code);
  }

  ngOnChanges(changes: SimpleChanges): void {
    console.log(changes);
  }
}
