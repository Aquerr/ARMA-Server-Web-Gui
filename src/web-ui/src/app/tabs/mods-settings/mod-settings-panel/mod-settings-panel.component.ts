import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { ModSettings } from "../../../model/mod-settings.model";
import { ModSettingsService } from "../../../service/mod-settings.service";
import { FormGroup, ReactiveFormsModule } from "@angular/forms";
import { EditModsSettingsFormService } from "./edit-mods-settings-form.service";
import { LoadingSpinnerMaskService } from "../../../service/loading-spinner-mask.service";
import { CodeJarContainer, NgxCodeJarComponent } from "ngx-codejar";
import hljs from "highlight.js";
import { NotificationService } from "../../../service/notification.service";
import {
  MatAccordion,
  MatExpansionModule,
  MatExpansionPanelDescription
} from "@angular/material/expansion";
import { MatIcon } from "@angular/material/icon";
import { MatFormFieldModule, MatLabel } from "@angular/material/form-field";
import { MatOption, MatSelect } from "@angular/material/select";
import { NgClass } from "@angular/common";
import { MatButton, MatIconButton } from "@angular/material/button";
import { MatInput } from "@angular/material/input";

@Component({
  selector: "app-mod-settings-panel",
  templateUrl: "./mod-settings-panel.component.html",
  styleUrl: "./mod-settings-panel.component.scss",
  standalone: true,
  imports: [
    MatFormFieldModule,
    MatAccordion,
    MatExpansionModule,
    MatIcon,
    MatExpansionPanelDescription,
    ReactiveFormsModule,
    MatSelect,
    MatOption,
    MatLabel,
    NgxCodeJarComponent,
    NgClass,
    MatIconButton,
    MatInput,
    MatButton
  ]
})
export class ModSettingsPanelComponent implements OnInit {
  @Input({ required: true }) modSettings!: ModSettings;
  @Output("deleted") modSettingsDeleted = new EventEmitter<number>();
  @Output("activated") modSettingsActivated = new EventEmitter<ModSettings>();

  public form!: FormGroup;

  constructor(
    private readonly modSettingsService: ModSettingsService,
    public readonly formService: EditModsSettingsFormService,
    private readonly maskService: LoadingSpinnerMaskService,
    private readonly notificationService: NotificationService
  ) {}

  ngOnInit() {
    this.form = this.formService.getForm();
    this.formService.setForm(this.form, this.modSettings, undefined);
  }

  onOpen() {
    if (!this.getContent() && this.modSettings.id) {
      this.maskService.show();
      this.modSettingsService.getModSettingsContent(this.modSettings.id).subscribe((response) => {
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
        this.modSettingsService
          .updateModSettings(this.getId(), this.formService.asModSettings(this.form))
          .subscribe((response) => {
            this.maskService.hide();
            this.notificationService.successNotification("Settings have been updated!");
          });
      } else {
        this.modSettingsService
          .createNewModSettings(this.formService.asModSettings(this.form))
          .subscribe((response) => {
            this.modSettings = {
              ...this.modSettings,
              id: response.id
            };
            this.maskService.hide();
            this.notificationService.successNotification("Settings have been added!");
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
        language: "sql"
      }).value;
    }
  }

  onCodeChange(code: string) {
    this.formService.setContentControl(this.form, code);
  }
}
