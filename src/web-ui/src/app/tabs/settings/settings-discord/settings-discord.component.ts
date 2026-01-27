import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnInit } from "@angular/core";
import { FormGroup, ReactiveFormsModule } from "@angular/forms";
import { DiscordSettingsFormService } from "./discord-settings-form.service";
import { DiscordSettingsService } from "../../../service/discord-settings.service";
import { LoadingSpinnerMaskService } from "../../../service/loading-spinner-mask.service";
import { NotificationService } from "../../../service/notification.service";
import { MatButton } from "@angular/material/button";
import { MatFormField, MatInput } from "@angular/material/input";
import { MatOption, MatSelect } from "@angular/material/select";
import { MatLabel } from "@angular/material/form-field";

@Component({
  selector: "app-settings-discord",
  templateUrl: "./settings-discord.component.html",
  styleUrl: "./settings-discord.component.scss",
  imports: [
    MatFormField,
    MatButton,
    ReactiveFormsModule,
    MatInput,
    MatFormField,
    MatSelect,
    MatLabel,
    MatOption
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SettingsDiscordComponent implements OnInit {
  public form!: FormGroup;

  private readonly formService: DiscordSettingsFormService = inject(DiscordSettingsFormService);
  private readonly discordSettingsService: DiscordSettingsService = inject(DiscordSettingsService);
  private readonly maskService: LoadingSpinnerMaskService = inject(LoadingSpinnerMaskService);
  private readonly notificationService: NotificationService = inject(NotificationService);
  private readonly changeDetectorRef: ChangeDetectorRef = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.form = this.formService.getForm();

    this.maskService.show();
    this.discordSettingsService.getDiscordSettings().subscribe((response) => {
      this.formService.setForm(this.form, response);
      this.maskService.hide();
      this.changeDetectorRef.markForCheck();
    });
  }

  save() {
    this.maskService.show();
    this.discordSettingsService
      .saveDiscordSettings(this.formService.asSettings(this.form))
      .subscribe(() => {
        this.maskService.hide();
        this.notificationService.successNotification("Settings have been saved!");
      });
  }
}
