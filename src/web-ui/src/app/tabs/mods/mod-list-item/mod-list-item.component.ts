import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from "@angular/core";
import { Mod, ModStatus } from "../../../model/mod.model";
import { ModDeleteConfirmDialogComponent } from "../mod-delete-confirm-dialog/mod-delete-confirm-dialog.component";
import { MatDialog } from "@angular/material/dialog";
import { LoadingSpinnerMaskService } from "../../../service/loading-spinner-mask.service";
import { ServerModsService } from "../../../service/server-mods.service";
import { ModForceUpdateConfirmDialogComponent } from "../mod-force-update-confirm-dialog/mod-force-update-confirm-dialog.component";
import { WorkshopService } from "../../../service/workshop.service";
import { NotificationService } from "../../../service/notification.service";
import {
  MatAccordion,
  MatExpansionPanel,
  MatExpansionPanelDescription,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle
} from "@angular/material/expansion";
import { NgOptimizedImage, NgStyle } from "@angular/common";
import { MatIconButton } from "@angular/material/button";
import { MatIcon } from "@angular/material/icon";
import { MatTooltip } from "@angular/material/tooltip";
import { FilesizePipe } from "../../../util/pipe/filesize.pipe";
import { MatCheckbox } from "@angular/material/checkbox";

@Component({
  selector: "app-mod-list-item",
  templateUrl: "./mod-list-item.component.html",
  imports: [
    MatAccordion,
    MatExpansionPanelTitle,
    MatExpansionPanelDescription,
    MatExpansionPanelHeader,
    NgStyle,
    MatIcon,
    MatIconButton,
    MatTooltip,
    FilesizePipe,
    MatCheckbox,
    NgOptimizedImage,
    MatExpansionPanel
  ],
  styleUrls: ["./mod-list-item.component.scss"],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ModListItemComponent {
  @Input() mod!: Mod;
  @Output() modDeleted: EventEmitter<Mod> = new EventEmitter<Mod>();
  expanded: boolean = false;

  readonly modStatus = ModStatus;

  constructor(
    private matDialog: MatDialog,
    private maskService: LoadingSpinnerMaskService,
    private modService: ServerModsService,
    private workshopService: WorkshopService,
    private notificationService: NotificationService
  ) {}

  showModDeleteConfirmationDialog(modName: string) {
    const dialogRef = this.matDialog.open(ModDeleteConfirmDialogComponent, {
      width: "250px",
      enterAnimationDuration: "200ms",
      exitAnimationDuration: "200ms"
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.deleteMod(modName);
      }
    });
  }

  deleteMod(modName: string) {
    this.maskService.show();
    this.modService.deleteMod(modName).subscribe(() => {
      this.maskService.hide();
      this.modDeleted.next(this.mod);
    });
  }

  showForceUpdateConfirmationDialog(fileId: number, modName: string) {
    const dialogRef = this.matDialog.open(ModForceUpdateConfirmDialogComponent, {
      width: "250px",
      enterAnimationDuration: "200ms",
      exitAnimationDuration: "200ms"
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.forceUpdateMod(fileId, modName);
      }
    });
  }

  private forceUpdateMod(modId: number, modName: string) {
    this.maskService.show();
    this.workshopService.installMod(modId, modName).subscribe(() => {
      this.maskService.hide();
      this.notificationService.infoNotification("Mod update scheduled");
    });
  }
}
