import { ChangeDetectionStrategy, ChangeDetectorRef, Component, computed, inject, input, output } from "@angular/core";
import { MatIconButton } from "@angular/material/button";
import { MatIcon } from "@angular/material/icon";
import { MatTooltip } from "@angular/material/tooltip";
import {
  MissionDeleteConfirmDialogComponent
} from "../mission-delete-confirm-dialog/mission-delete-confirm-dialog.component";
import { Mission } from "@model/mission.model";
import { MissionModifyDialogComponent } from "../mission-modify-dialog/mission-modify-dialog.component";
import { DialogService } from "@service/dialog.service";
import { LoadingSpinnerMaskService } from "@service/loading-spinner-mask.service";
import { NotificationService } from "@service/notification.service";
import { ServerMissionsService } from "@service/server-missions.service";
import { FilesizePipe } from "@app/util/pipe/filesize.pipe";
import { PermissionService } from "@service/permission.service";
import { AswgAuthority } from "@model/authority.model";
import { FileDownloadMonitorService } from "@service/file-download-monitor.service";

@Component({
  selector: "app-mission-list-item",
  templateUrl: "./mission-list-item.component.html",
  imports: [
    MatIcon,
    MatIconButton,
    MatTooltip,
    FilesizePipe
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ["./mission-list-item.component.scss"]
})
export class MissionListItemComponent {
  public mission = input.required<Mission>();
  public missionDeleted = output<Mission>();

  public missionDisplayName = computed(() => {
    const mission = this.mission();
    return mission.name || mission.template;
  });

  private readonly changeDetectorRef = inject(ChangeDetectorRef);
  private readonly dialogService = inject(DialogService);
  private readonly maskService = inject(LoadingSpinnerMaskService);
  private readonly notificationService = inject(NotificationService);
  private readonly missionsService = inject(ServerMissionsService);
  private readonly permissionService = inject(PermissionService);
  private readonly downloadMonitorService = inject(FileDownloadMonitorService);

  showMissionModifyDialog() {
    this.dialogService.open(MissionModifyDialogComponent, (mission: Mission) => {
      if (mission) {
        this.updateMission(mission);
        this.changeDetectorRef.markForCheck();
      }
    }, { mission: this.mission() }, {
      minWidth: "500px",
      enterAnimationDuration: "200ms",
      exitAnimationDuration: "200ms",
      data: this.mission()
    });
  }

  showMissionDeleteConfirmationDialog(): void {
    this.dialogService.open(MissionDeleteConfirmDialogComponent, (result) => {
      if (result) {
        this.deleteMission();
      }
    }, {}, {
      width: "250px",
      enterAnimationDuration: "200ms",
      exitAnimationDuration: "200ms"
    });
  }

  deleteMission() {
    this.maskService.show();
    this.missionDeleted.emit(this.mission());
  }

  private updateMission(mission: Mission) {
    this.maskService.show();
    this.missionsService.updateMission(mission.id, mission).subscribe(() => {
      this.maskService.hide();
      this.notificationService.successNotification("Mission updated!");
    });
  }

  public downloadMission() {
    if (!this.permissionService.hasAllAuthorities([AswgAuthority.MISSIONS_DOWNLOAD], true)) {
      return;
    }

    const fileName = this.mission().template + ".pbo";

    if (this.downloadMonitorService.isInQueue(fileName)) {
      return;
    }

    this.missionsService.downloadMission(this.mission().id)
      .subscribe(this.downloadMonitorService.monitorFileDownload(fileName));
  }
}
