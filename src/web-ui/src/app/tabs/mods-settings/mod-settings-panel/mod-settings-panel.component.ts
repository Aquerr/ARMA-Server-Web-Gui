import {
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild
} from "@angular/core";
import { ModSettings } from "../../../model/mod-settings.model";
import { ModSettingsService } from "../../../service/mod-settings.service";
import { FormGroup, ReactiveFormsModule } from "@angular/forms";
import { EditModsSettingsFormService } from "./edit-mods-settings-form.service";
import { LoadingSpinnerMaskService } from "../../../service/loading-spinner-mask.service";
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
import { CodeJar } from "codejar";
import { withLineNumbers } from "codejar-linenumbers";

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
    NgClass,
    MatIconButton,
    MatInput,
    MatButton
  ]
})
export class ModSettingsPanelComponent implements OnInit, AfterViewInit {
  @Input({ required: true }) modSettings!: ModSettings;
  @Output() modSettingsDeleted = new EventEmitter<number>();
  @Output() modSettingsActivated = new EventEmitter<ModSettings>();

  @ViewChild("code") codeElement!: ElementRef<HTMLDivElement>;
  public form!: FormGroup;

  private jar!: CodeJar;

  constructor(
    private readonly modSettingsService: ModSettingsService,
    public readonly formService: EditModsSettingsFormService,
    private readonly maskService: LoadingSpinnerMaskService,
    private readonly notificationService: NotificationService
  ) {}

  ngAfterViewInit(): void {
    this.prepareCodeJar();
  }

  ngOnInit() {
    this.form = this.formService.getForm();
    this.formService.setForm(this.form, this.modSettings, undefined);
  }

  onOpen() {
    if (!this.getContent() && this.modSettings.id) {
      this.maskService.show();
      this.modSettingsService.getModSettingsContent(this.modSettings.id).subscribe((response) => {
        // this.formService.setContentControl(this.form, response.content);
        this.jar.updateCode(response.content);
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
          .subscribe(() => {
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
    return this.formService.getIdControl(this.form).value!;
  }

  getName() {
    return this.formService.getNameControl(this.form).value;
  }

  getActive() {
    return this.formService.getActiveControl(this.form).value;
  }

  getContent() {
    return this.formService.getContentControl(this.form).value!;
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

  static highlightMethod(editor: HTMLElement) {
    if (editor.textContent !== null && editor.textContent !== undefined) {
      editor.innerHTML = hljs.highlight(editor.textContent, {
        language: "sql"
      }).value;
    }
  }

  private prepareCodeJar() {
    this.jar = CodeJar(this.codeElement.nativeElement, withLineNumbers(ModSettingsPanelComponent.highlightMethod), {
      tab: "\t"
    });
    this.jar.onUpdate((code) => {
      this.formService.setContentControl(this.form, code);
    });
  }
}
