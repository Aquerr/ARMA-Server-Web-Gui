import { Component, inject, OnInit } from "@angular/core";
import { FormGroup, ReactiveFormsModule } from "@angular/forms";
import { DiscordSettingsFormService } from "./discord-settings-form.service";
import { DiscordSettingsService } from "../../../service/discord-settings.service";
import { MaskService } from "../../../service/mask.service";
import { NotificationService } from "../../../service/notification.service";
import { MatFormField, MatLabel } from "@angular/material/form-field";
import { MatOption, MatSelect } from "@angular/material/select";
import { MatInput } from "@angular/material/input";
import { MatButton } from "@angular/material/button";

@Component({
  selector: "app-settings-discord",
  templateUrl: "./settings-discord.component.html",
  styleUrl: "./settings-discord.component.scss",
  standalone: true,
  imports: [MatFormField, MatLabel, MatInput, MatFormField, MatButton, MatSelect, MatOption, ReactiveFormsModule]
})
export class SettingsDiscordComponent implements OnInit {
  public form!: FormGroup;

  private readonly formService: DiscordSettingsFormService = inject(DiscordSettingsFormService);
  private readonly discordSettingsService: DiscordSettingsService = inject(DiscordSettingsService);
  private readonly maskService: MaskService = inject(MaskService);
  private readonly notificationService: NotificationService = inject(NotificationService);

  ngOnInit(): void {
    this.form = this.formService.getForm();

    this.maskService.show();
    this.discordSettingsService.getDiscordSettings().subscribe((response) => {
      this.formService.setForm(this.form, response);
      this.maskService.hide();
    });
  }

  save() {
    this.maskService.show();
    this.discordSettingsService
      .saveDiscordSettings(this.formService.asSettings(this.form))
      .subscribe((response) => {
        this.maskService.hide();
        this.notificationService.successNotification("Settings have been saved!");
      });
  }
}
