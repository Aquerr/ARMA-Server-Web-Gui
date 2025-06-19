import { Component, inject, OnInit } from "@angular/core";
import { MatButton } from "@angular/material/button";
import { FormGroup, ReactiveFormsModule } from "@angular/forms";
import { MatFormField, MatLabel } from "@angular/material/form-field";
import { MatInput } from "@angular/material/input";
import { MaskService } from "../../../service/mask.service";
import { NotificationService } from "../../../service/notification.service";
import { SteamSettingsFormService } from "./steam-settings-form.service";
import { SteamSettingsService } from "../../../service/steam-settings.service";
import { MatTooltip } from "@angular/material/tooltip";

@Component({
  selector: "app-settings-steam",
  imports: [
    MatButton,
    ReactiveFormsModule,
    MatFormField,
    MatLabel,
    MatInput,
    MatTooltip
  ],
  templateUrl: "./settings-steam.component.html",
  styleUrl: "./settings-steam.component.scss"
})
export class SettingsSteamComponent implements OnInit {
  public form!: FormGroup;

  private readonly formService: SteamSettingsFormService = inject(SteamSettingsFormService);
  private readonly steamSettingsService: SteamSettingsService = inject(SteamSettingsService);
  private readonly maskService: MaskService = inject(MaskService);
  private readonly notificationService: NotificationService = inject(NotificationService);

  ngOnInit(): void {
    this.form = this.formService.getForm();

    this.maskService.show();
    this.steamSettingsService.getSteamSettings().subscribe((response) => {
      this.formService.setForm(this.form, response);
      this.maskService.hide();
    });
  }

  save() {
    this.maskService.show();
    this.steamSettingsService
      .saveSteamSettings(this.formService.asSettings(this.form))
      .subscribe((response) => {
        this.maskService.hide();
        this.notificationService.successNotification("Settings have been saved!");
      });
  }
}
