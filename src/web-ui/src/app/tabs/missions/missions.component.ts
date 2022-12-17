import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {UploadMissionComponent} from "./upload-mission/upload-mission.component";
import {Subject, Subscription} from "rxjs";
import {ServerMissionsService} from "../../service/server-missions.service";
import {MaskService} from "../../service/mask.service";
import {MatDialog} from "@angular/material/dialog";
import {
  MissionDeleteConfirmDialogComponent
} from "./mission-delete-confirm-dialog/mission-delete-confirm-dialog.component";
import {NotificationService} from "../../service/notification.service";
import {MissionModifyDialogComponent} from "./mission-modify-dialog/mission-modify-dialog.component";
import {Mission} from "../../model/mission.model";
import {FormControl} from "@angular/forms";
import {CdkDragDrop, moveItemInArray, transferArrayItem} from "@angular/cdk/drag-drop";

@Component({
  selector: 'app-missions',
  templateUrl: './missions.component.html',
  styleUrls: ['./missions.component.css']
})
export class MissionsComponent implements OnInit, OnDestroy {

  @ViewChild('uploadMission') uploadMissionComponent!: UploadMissionComponent;

  disabledMissions: Mission[] = [];
  enabledMissions: Mission[] = [];
  filteredDisabledMissions: Mission[] = [];
  filteredEnabledMissions: Mission[] = [];

  reloadMissionsDataSubject: Subject<any>;
  reloadMissionDataSubscription!: Subscription;
  searchBoxControl!: FormControl;

  constructor(private missionsService: ServerMissionsService,
              private maskService: MaskService,
              private notificationService: NotificationService,
              private matDialog: MatDialog) {
    this.reloadMissionsDataSubject = new Subject();
  }

  ngOnInit(): void {
    this.searchBoxControl = new FormControl('');
    this.searchBoxControl.valueChanges.subscribe(value => {
      this.filterMissions(value);
    });
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
      this.filteredDisabledMissions = [...this.disabledMissions];
      this.filteredEnabledMissions = [...this.enabledMissions];
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
    console.log(this.enabledMissions);
    this.missionsService.saveEnabledMissions({missions: this.enabledMissions}).subscribe(response => {
      this.maskService.hide();
      this.notificationService.successNotification('Active mission list saved!', 'Success');
    });
  }

  showMissionModifyDialog(mission: Mission) {
    const dialogRef = this.matDialog.open(MissionModifyDialogComponent, {
      // width: '250px',
      enterAnimationDuration: '200ms',
      exitAnimationDuration: '200ms',
      data: mission.parameters
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log("Modified mission");
      console.log(result);
    });
  }

  filterMissions(searchPhrase: String) {
    this.filteredEnabledMissions = this.enabledMissions.filter(mission => mission.name.toLowerCase().includes(searchPhrase.toLowerCase()));
    this.filteredDisabledMissions = this.disabledMissions.filter(mission => mission.name.toLowerCase().includes(searchPhrase.toLowerCase()));
  }

  onMissionItemDropped(event: CdkDragDrop<Mission[]>) {
    if (event.previousContainer === event.container){
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      let movedMod = event.previousContainer.data[event.previousIndex];
      if (event.previousContainer.id == 'enabled-missions-list') {
        this.enabledMissions.forEach((value, index) => {
          if (value == movedMod) this.enabledMissions.splice(index, 1);
        });
        this.disabledMissions.push(movedMod);
      } else {
        this.disabledMissions.forEach((value, index) => {
          if (value == movedMod) this.disabledMissions.splice(index, 1);
        });
        this.enabledMissions.push(movedMod);
      }

      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex,
      );
    }
  }
}
