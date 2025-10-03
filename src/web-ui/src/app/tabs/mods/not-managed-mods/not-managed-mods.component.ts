import { Component, EventEmitter, inject, Input, Output } from "@angular/core";
import { Mod } from "../../../model/mod.model";
import { MatButton } from "@angular/material/button";
import { DialogService } from "../../../service/dialog.service";
import { LoadingSpinnerMaskService } from "../../../service/loading-spinner-mask.service";
import { NotificationService } from "../../../service/notification.service";
import { ServerModsService } from "../../../service/server-mods.service";
import { MatIcon } from "@angular/material/icon";

@Component({
  selector: "app-not-managed-mods",
  imports: [MatButton, MatIcon],
  templateUrl: "./not-managed-mods.component.html",
  styleUrl: "./not-managed-mods.component.scss"
})
export class NotManagedModsComponent {
  @Input() mods!: Mod[];
  @Output() notManagedModsChanged = new EventEmitter<void>();

  private readonly dialogService: DialogService = inject(DialogService);
  private readonly maskService: LoadingSpinnerMaskService = inject(LoadingSpinnerMaskService);
  private readonly notificationService: NotificationService = inject(NotificationService);
  private readonly modService: ServerModsService = inject(ServerModsService);

  registerNotManagedMod(mod: Mod) {
    this.dialogService.openCommonConfirmationDialog(
      { question: `Are you sure you want to add <strong>${mod.name}</strong> to managed mods?` },
      (dialogResult) => {
        if (dialogResult) {
          this.maskService.show();
          this.modService.manageMod(mod.name).subscribe(() => {
            this.maskService.hide();
            this.notManagedModsChanged.emit();
            this.notificationService.successNotification(`Mod ${mod.name} is not managed by ASWG`);
          });
        }
      }
    );
  }

  deleteNotManagedMod(mod: Mod) {
    this.dialogService.openCommonConfirmationDialog(
      { question: `Are you sure you want to delete the folder for <strong>${mod.name}</strong>?` },
      (dialogResult) => {
        if (dialogResult) {
          this.maskService.show();
          this.modService.deleteNotManagedMod(mod.directoryName).subscribe(() => {
            this.maskService.hide();
            this.notManagedModsChanged.emit();
            this.notificationService.successNotification(
              `Mod directory for ${mod.name} has been deleted!`
            );
          });
        }
      }
    );
  }
}
