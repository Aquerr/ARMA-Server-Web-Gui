import {
  Component,
  OnInit,
  ChangeDetectionStrategy,
  output,
  model, viewChild
} from "@angular/core";
import { ModSettings } from "@model/mod-settings.model";
import { ModSettingsService } from "@service/mod-settings.service";
import { FormGroup, ReactiveFormsModule } from "@angular/forms";
import { EditModSettingsFormControls, EditModsSettingsFormService } from "./edit-mods-settings-form.service";
import { LoadingSpinnerMaskService } from "@service/loading-spinner-mask.service";
import { NotificationService } from "@service/notification.service";
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
import { AswgSqfEditorComponent } from "@common-ui/aswg-sqf-editor/aswg-sqf-editor.component";

@Component({
  selector: "app-mod-settings-panel",
  templateUrl: "./mod-settings-panel.component.html",
  styleUrl: "./mod-settings-panel.component.scss",
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
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
    NgClass,
    MatIconButton,
    MatInput,
    MatButton,
    AswgSqfEditorComponent
  ]
})
export class ModSettingsPanelComponent implements OnInit {
  public readonly modSettings = model.required<ModSettings>();
  public readonly modSettingsDeleted = output<number>();
  public readonly modSettingsActivated = output<ModSettings>();

  codeElement = viewChild.required<AswgSqfEditorComponent>("codeEditor");

  public form: FormGroup<EditModSettingsFormControls>;

  constructor(
    private readonly modSettingsService: ModSettingsService,
    public readonly formService: EditModsSettingsFormService,
    private readonly maskService: LoadingSpinnerMaskService,
    private readonly notificationService: NotificationService
  ) {
    this.form = this.formService.getForm();
  }

  ngOnInit() {
    this.formService.setForm(this.form, this.modSettings(), undefined);
  }

  onOpen() {
    if (!this.form.controls.content.value && this.modSettings().id) {
      this.maskService.show();
      this.modSettingsService.getModSettingsContent(this.modSettings().id!).subscribe((response) => {
        this.codeElement().setCode(response.content);
        this.maskService.hide();
      });
    }
  }

  save() {
    this.form.markAllAsTouched();
    if (this.form.valid) {
      this.maskService.show();
      if (this.form.controls.id.value) {
        this.modSettingsService
          .updateModSettings(this.form.controls.id.value, this.formService.asModSettings(this.form))
          .subscribe(() => {
            this.maskService.hide();
            this.notificationService.successNotification("Settings have been updated!");
          });
      } else {
        this.modSettingsService
          .createNewModSettings(this.formService.asModSettings(this.form))
          .subscribe((response) => {
            this.modSettings.update((oldModSettings: ModSettings) => {
              return {
                ...oldModSettings,
                id: response.id
              };
            });
            this.maskService.hide();
            this.notificationService.successNotification("Settings have been added!");
          });
      }
    }
  }

  deleteClick(event: MouseEvent) {
    event.stopPropagation();
    if (this.form.controls.id.value) {
      this.modSettingsDeleted.emit(this.form.controls.id.value);
    }
  }

  toggleActive(event: MouseEvent) {
    event.stopPropagation();
    this.form.controls.active.setValue(!this.form.controls.active.value);
    this.modSettingsActivated.emit(this.formService.asModSettings(this.form));
  }

  protected onCodeChanged(code: string) {
    this.form.controls.content.setValue(code);
  }
}
