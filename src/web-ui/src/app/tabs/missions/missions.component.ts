import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {UploadMissionComponent} from "./upload-mission/upload-mission.component";
import {Subject, Subscription} from "rxjs";
import {ServerMissionsService} from "../../service/server-missions.service";
import {MaskService} from "../../service/mask.service";
import {MatDialog} from "@angular/material/dialog";
import {
  MissionDeleteConfirmDialogComponent
} from "./list-missions/mission-delete-confirm-dialog/mission-delete-confirm-dialog.component";

@Component({
  selector: 'app-missions',
  templateUrl: './missions.component.html',
  styleUrls: ['./missions.component.css']
})
export class MissionsComponent implements OnInit, OnDestroy {

  @ViewChild('uploadMission') uploadMissionComponent!: UploadMissionComponent;

  missions: string[] = [];
  reloadMissionsDataSubject: Subject<any>;
  reloadMissionDataSubscription!: Subscription;

  constructor(private missionsService: ServerMissionsService,
              private maskService: MaskService,
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
    console.log('GOT FILE! ' + file);
    this.uploadMissionComponent.uploadFile(file);
  }

  onMissionUploaded() {
    this.reloadMissionsDataSubject.next(null);
  }

  private reloadMissions(): void {
    this.maskService.show();
    this.missionsService.getInstalledMissions().subscribe(response => {
      this.missions = response.missions;
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
}
