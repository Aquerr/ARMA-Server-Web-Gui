import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {UploadMissionComponent} from "./upload-mission/upload-mission.component";
import {Subject, Subscription} from "rxjs";
import {ServerMissionsService} from "../../service/server-missions.service";
import {MaskService} from "../../service/mask.service";
import {MatDialog} from "@angular/material/dialog";
import {
  MissionDeleteConfirmDialogComponent
} from "./mission-delete-confirm-dialog/mission-delete-confirm-dialog.component";
import {AswgDragDropListComponent} from "../../common-ui/aswg-drag-drop-list/aswg-drag-drop-list.component";
import {NotificationService} from "../../service/notification.service";
import {MissionModifyDialogComponent} from "./mission-modify-dialog/mission-modify-dialog.component";

@Component({
  selector: 'app-missions',
  templateUrl: './missions.component.html',
  styleUrls: ['./missions.component.css']
})
export class MissionsComponent implements OnInit, OnDestroy {

  @ViewChild('uploadMission') uploadMissionComponent!: UploadMissionComponent;
  @ViewChild('enabledMissionsList') enabledMissionsList!: AswgDragDropListComponent;

  disabledMissions: string[] = [];
  enabledMissions: string[] = [];
  reloadMissionsDataSubject: Subject<any>;
  reloadMissionDataSubscription!: Subscription;

  constructor(private missionsService: ServerMissionsService,
              private maskService: MaskService,
              private notificationService: NotificationService,
              private matDialog: MatDialog) {
    this.reloadMissionsDataSubject = new Subject();
  }

  ngOnInit(): void {
    this.reloadMissions();
    this.reloadMissionDataSubscription = this.reloadMissionsDataSubject.subscribe(() => {
      this.reloadMissions();
    });
  }

  ngOnDestroy(): void {
    this.reloadMissionDataSubscription.unsubscribe();
  }

  onFileDropped(file: File) {
    this.uploadMissionComponent.uploadFile(file);
  }

  onMissionUploaded() {
    this.reloadMissionsDataSubject.next(null);
  }

  private reloadMissions(): void {
    this.maskService.show();
    this.missionsService.getInstalledMissions().subscribe(response => {
      this.disabledMissions = response.disabledMissions;
      this.enabledMissions = response.enabledMissions;
      this.maskService.hide();
    });
  }

  showMissionDeleteConfirmationDialog(missionName: string): void {
    const dialogRef = this.matDialog.open(MissionDeleteConfirmDialogComponent, {
      width: '250px',
      enterAnimationDuration: '200ms',
      exitAnimationDuration: '200ms'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.deleteMission(missionName);
      }
    });
  }

  deleteMission(missionName: string) {
    this.maskService.show();
    this.missionsService.deleteMission(missionName).subscribe(response => {
      this.maskService.hide();
      this.reloadMissionsDataSubject.next(null);
    });
  }

  save() {
    this.maskService.show();
    console.log(this.enabledMissionsList.items);
    this.missionsService.saveEnabledMissions({missions: this.enabledMissionsList.items}).subscribe(response => {
      this.maskService.hide();
      this.notificationService.successNotification('Active mission list saved!', 'Success');
    });
  }

  showMissionModifyDialog(missionName: string) {
    const dialogRef = this.matDialog.open(MissionModifyDialogComponent, {
      // width: '250px',
      enterAnimationDuration: '200ms',
      exitAnimationDuration: '200ms'
    });

    dialogRef.afterClosed().subscribe(result => {

    });
  }
}
