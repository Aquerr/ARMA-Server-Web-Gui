import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnInit } from "@angular/core";
import { MatButton } from "@angular/material/button";
import { FormGroup, ReactiveFormsModule } from "@angular/forms";
import { MatFormField, MatLabel } from "@angular/material/form-field";
import { MatInput } from "@angular/material/input";
import { LoadingSpinnerMaskService } from "../../../service/loading-spinner-mask.service";
import { NotificationService } from "../../../service/notification.service";
import { SteamSettingsFormService } from "./steam-settings-form.service";
import { SteamSettingsService } from "../../../service/steam-settings.service";
import { MatTooltip } from "@angular/material/tooltip";
import {
  PasswordChangeDialogComponent
} from "../../../common-ui/password-change-dialog/password-change-dialog.component";
import { DialogService } from "../../../service/dialog.service";

@Component({
  selector: "app-settings-steam",
  imports: [MatButton, ReactiveFormsModule, MatFormField, MatLabel, MatInput, MatTooltip],
  templateUrl: "./settings-steam.component.html",
  styleUrl: "./settings-steam.component.scss",
  providers: [PasswordChangeDialogComponent],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SettingsSteamComponent implements OnInit {
  public form!: FormGroup;

  private readonly formService: SteamSettingsFormService = inject(SteamSettingsFormService);
  private readonly steamSettingsService: SteamSettingsService = inject(SteamSettingsService);
  private readonly maskService: LoadingSpinnerMaskService = inject(LoadingSpinnerMaskService);
  private readonly notificationService: NotificationService = inject(NotificationService);
  private readonly dialogService: DialogService = inject(DialogService);
  private readonly changeDetectorRef: ChangeDetectorRef = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.form = this.formService.getForm();

    this.maskService.show();
    this.steamSettingsService.getSteamSettings().subscribe((response) => {
      this.formService.setForm(this.form, response);
      this.maskService.hide();
      this.changeDetectorRef.markForCheck();
    });
  }

  save() {
    this.maskService.show();
    this.steamSettingsService
      .saveSteamSettings(this.formService.asSettings(this.form))
      .subscribe(() => {
        this.maskService.hide();
        this.notificationService.successNotification("Settings have been saved!");
      });
  }

  public showEditSteamCmdPasswordModal() {
    this.dialogService.open(PasswordChangeDialogComponent, (dialogResult: string) => {
      if (dialogResult) {
        this.maskService.show();
        this.steamSettingsService.updateSteamPassword(dialogResult).subscribe(() => {
          this.maskService.hide();
          this.notificationService.successNotification("SteamCmd password has been updated!");
        });
      }
    });
  }
}
