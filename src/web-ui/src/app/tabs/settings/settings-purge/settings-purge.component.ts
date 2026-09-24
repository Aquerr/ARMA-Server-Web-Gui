import { Component, inject } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { DataPurgeService } from "@service/data-purge.service";
import { DialogService } from "@service/dialog.service";
import { PermissionService } from "@service/permission.service";
import { AswgAuthority } from "@model/authority.model";
import { LoadingSpinnerMaskService } from "@service/loading-spinner-mask.service";
import { NotificationService } from "@service/notification.service";
import { finalize } from "rxjs";
import {
  PurgeMissionsDialogComponent
} from "@app/tabs/settings/settings-purge/purge-missions-dialog/purge-missions-dialog.component";
import {
  PurgeModsDialogComponent
} from "@app/tabs/settings/settings-purge/purge-mods-dialog/purge-mods-dialog.component";
import { MatIcon } from "@angular/material/icon";

@Component({
  selector: "app-settings-purge",
  imports: [
    FormsModule,
    MatIcon
  ],
  templateUrl: "./settings-purge.component.html",
  styleUrl: "./settings-purge.component.scss"
})
export class SettingsPurgeComponent {
  private readonly dialogService = inject(DialogService);
  private readonly permissionsService = inject(PermissionService);
  private readonly dataPurgeService = inject(DataPurgeService);
  private readonly loadingSpinnerMaskService = inject(LoadingSpinnerMaskService);
  private readonly notificationService = inject(NotificationService);

  protected purgeMissions() {
    if (!this.permissionsService.hasAllAuthorities([AswgAuthority.MISSIONS_PURGE], true)) {
      return;
    }

    // Confirmation
    this.dialogService.open<PurgeMissionsDialogComponent, { deleteFiles: boolean }>(PurgeMissionsDialogComponent, (dialogResult) => {
      if (dialogResult) {
        this.loadingSpinnerMaskService.show();
        this.dataPurgeService.purgeMissions(dialogResult.deleteFiles).pipe(finalize(() => this.loadingSpinnerMaskService.hide())).subscribe(() => {
          this.notificationService.successNotification("All missions have been deleted!");
        });
      }
    });
  }

  protected purgeMods() {
    if (!this.permissionsService.hasAllAuthorities([AswgAuthority.MODS_PURGE], true)) {
      return;
    }

    // Confirmation
    this.dialogService.open<PurgeModsDialogComponent, { deleteFiles: boolean }>(PurgeModsDialogComponent, (dialogResult) => {
      if (dialogResult) {
        this.loadingSpinnerMaskService.show();
        this.dataPurgeService.purgeMods(dialogResult.deleteFiles).pipe(finalize(() => this.loadingSpinnerMaskService.hide())).subscribe(() => {
          this.notificationService.successNotification("All mods have been deleted!");
        });
      }
    });
  }

  protected purgeModPresets() {
    if (!this.permissionsService.hasAllAuthorities([AswgAuthority.MOD_PRESETS_PURGE], true)) {
      return;
    }

    // Confirmation
    const closeCallback = (dialogResult: boolean) => {
      if (dialogResult) {
        this.loadingSpinnerMaskService.show();
        this.dataPurgeService.purgeModPresets()
          .pipe(finalize(() => this.loadingSpinnerMaskService.hide()))
          .subscribe(() => {
            this.notificationService.successNotification("All mod presets have been deleted!");
          });
      }
    };

    this.dialogService.openCommonConfirmationDialog({
      headerLabel: "Confirmation",
      question: "Do you really want to delete all mod presets managed by ASWG?"
    }, closeCallback);
  }
}
