import { ChangeDetectorRef, Component, computed, inject, input, output, ViewEncapsulation } from "@angular/core";
import { MatIconButton } from "@angular/material/button";
import { MatIcon } from "@angular/material/icon";
import { MatTooltip } from "@angular/material/tooltip";
import {
  MissionDeleteConfirmDialogComponent
} from "../mission-delete-confirm-dialog/mission-delete-confirm-dialog.component";
import { Mission } from "../../../model/mission.model";
import { MissionModifyDialogComponent } from "../mission-modify-dialog/mission-modify-dialog.component";
import { DialogService } from "../../../service/dialog.service";
import { LoadingSpinnerMaskService } from "../../../service/loading-spinner-mask.service";
import { NotificationService } from "../../../service/notification.service";
import { ServerMissionsService } from "../../../service/server-missions.service";
import { FilesizePipe } from "../../../util/pipe/filesize.pipe";

@Component({
  selector: "app-mission-list-item",
  templateUrl: "./mission-list-item.component.html",
  imports: [
    MatIcon,
    MatIconButton,
    MatTooltip,
    FilesizePipe
  ],
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
        this.deleteMission(this.mission().template);
      }
    }, {}, {
      width: "250px",
      enterAnimationDuration: "200ms",
      exitAnimationDuration: "200ms"
    });
  }

  deleteMission(missionTemplate: string) {
    this.maskService.show();
    this.missionsService.deleteMission(missionTemplate).subscribe(() => {
      this.maskService.hide();
      this.missionDeleted.emit(this.mission());
    });
  }

  private updateMission(mission: Mission) {
    this.maskService.show();
    this.missionsService.updateMission(mission.id, mission).subscribe(() => {
      this.maskService.hide();
      this.notificationService.successNotification("Mission updated!");
    });
  }
}
