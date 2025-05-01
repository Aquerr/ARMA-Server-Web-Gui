import { Component, OnDestroy, OnInit, ViewChild } from "@angular/core";
import { MissionUploadButtonComponent } from "./upload-mission/mission-upload-button.component";
import { Subject, Subscription } from "rxjs";
import { ServerMissionsService } from "../../service/server-missions.service";
import { MaskService } from "../../service/mask.service";
import { MatDialog } from "@angular/material/dialog";
import { MissionDeleteConfirmDialogComponent } from "./mission-delete-confirm-dialog/mission-delete-confirm-dialog.component";
import { NotificationService } from "../../service/notification.service";
import { MissionModifyDialogComponent } from "./mission-modify-dialog/mission-modify-dialog.component";
import { Mission } from "../../model/mission.model";
import { FormControl } from "@angular/forms";
import { CdkDragDrop, moveItemInArray, transferArrayItem } from "@angular/cdk/drag-drop";
import { MissionUploadService } from "./service/mission-upload.service";
import { NewMissionDialogComponent } from "./new-mission-dialog/new-mission-dialog.component";

@Component({
  selector: "app-missions",
  templateUrl: "./missions.component.html",
  styleUrls: ["./missions.component.scss"],
  standalone: false
})
export class MissionsComponent implements OnInit, OnDestroy {
  @ViewChild("uploadMission") uploadMissionComponent!: MissionUploadButtonComponent;

  disabledMissions: Mission[] = [];
  enabledMissions: Mission[] = [];
  filteredDisabledMissions: Mission[] = [];
  filteredEnabledMissions: Mission[] = [];

  reloadMissionsDataSubject: Subject<any>;
  reloadMissionDataSubscription!: Subscription;
  missionUploadSubscription!: Subscription;
  searchBoxControl!: FormControl;

  constructor(
    private missionsService: ServerMissionsService,
    private maskService: MaskService,
    private notificationService: NotificationService,
    private matDialog: MatDialog,
    private missionUploadService: MissionUploadService
  ) {
    this.reloadMissionsDataSubject = new Subject();
    this.reloadMissionDataSubscription = this.reloadMissionsDataSubject.subscribe(() => {
      this.reloadMissions();
    });
    this.missionUploadSubscription = this.missionUploadService.fileUploadedSubject.subscribe(
      (file) => {
        if (file) {
          this.reloadMissionsDataSubject.next(null);
        }
      }
    );
  }

  ngOnInit(): void {
    this.searchBoxControl = new FormControl("");
    this.searchBoxControl.valueChanges.subscribe((value) => {
      this.filterMissions(value);
    });
    this.reloadMissions();
  }

  ngOnDestroy(): void {
    this.reloadMissionDataSubscription.unsubscribe();
    this.missionUploadSubscription.unsubscribe();
  }

  onFileDropped(file: File) {
    this.missionUploadService.uploadMission(file);
  }

  private reloadMissions(): void {
    this.maskService.show();
    this.missionsService.getInstalledMissions().subscribe((response) => {
      this.disabledMissions = response.disabledMissions;
      this.enabledMissions = response.enabledMissions;
      this.filteredDisabledMissions = [...this.disabledMissions];
      this.filteredEnabledMissions = [...this.enabledMissions];
      this.maskService.hide();
    });
  }

  showMissionDeleteConfirmationDialog(missionTemplate: string): void {
    const dialogRef = this.matDialog.open(MissionDeleteConfirmDialogComponent, {
      width: "250px",
      enterAnimationDuration: "200ms",
      exitAnimationDuration: "200ms"
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.deleteMission(missionTemplate);
      }
    });
  }

  deleteMission(missionTemplate: string) {
    this.maskService.show();
    this.missionsService.deleteMission(missionTemplate).subscribe((response) => {
      this.maskService.hide();
      this.notificationService.successNotification("Mission has been deleted!");
      this.reloadMissionsDataSubject.next(null);
    });
  }

  save() {
    this.maskService.show();
    this.missionsService
      .saveEnabledMissions({ missions: this.enabledMissions })
      .subscribe((response) => {
        this.maskService.hide();
        this.notificationService.successNotification("Active mission list saved!", "Success");
      });
  }

  showMissionModifyDialog(mission: Mission) {
    const dialogRef = this.matDialog.open(MissionModifyDialogComponent, {
      minWidth: "500px",
      enterAnimationDuration: "200ms",
      exitAnimationDuration: "200ms",
      data: mission
    });

    dialogRef.afterClosed().subscribe((mission: Mission) => {
      if (mission) {
        this.updateMission(mission);
      }
    });
  }

  filterMissions(searchPhrase: string) {
    this.filteredEnabledMissions = this.enabledMissions.filter((mission) =>
      mission.template.toLowerCase().includes(searchPhrase.toLowerCase())
    );
    this.filteredDisabledMissions = this.disabledMissions.filter((mission) =>
      mission.template.toLowerCase().includes(searchPhrase.toLowerCase())
    );
  }

  onMissionItemDropped(event: CdkDragDrop<Mission[]>) {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      let movedMission = event.previousContainer.data[event.previousIndex];
      if (event.previousContainer.id == "enabled-missions-list") {
        this.enabledMissions.forEach((value, index) => {
          if (value == movedMission) this.enabledMissions.splice(index, 1);
        });
        this.disabledMissions.push(movedMission);
      } else {
        this.disabledMissions.forEach((value, index) => {
          if (value == movedMission) this.disabledMissions.splice(index, 1);
        });
        this.enabledMissions.push(movedMission);
      }

      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex
      );
    }
  }

  addNewMission() {
    const dialogRef = this.matDialog.open(NewMissionDialogComponent, {
      width: "450px",
      enterAnimationDuration: "200ms",
      exitAnimationDuration: "200ms"
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result.file) {
        this.onFileDropped(result.file);
      } else if (result.template) {
        this.addBuiltInMission(result.name, result.template);
      }
    });
  }

  private addBuiltInMission(name: string, template: string) {
    this.maskService.show();
    this.missionsService.addTemplateMission(name, template).subscribe((response) => {
      this.maskService.hide();
      this.reloadMissions();
      this.notificationService.successNotification("Mission added!");
    });
  }

  private updateMission(mission: Mission) {
    this.maskService.show();
    this.missionsService.updateMission(mission.id, mission).subscribe((response) => {
      this.maskService.hide();
      this.notificationService.successNotification("Mission updated!");
    });
  }

  getMissionNameForDisplay(mission: Mission) {
    return mission.name || mission.template;
  }
}
