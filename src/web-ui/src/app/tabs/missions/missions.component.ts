import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MissionUploadButtonComponent} from "./upload-mission/mission-upload-button.component";
import {Subject, Subscription} from "rxjs";
import {ServerMissionsService} from "../../service/server-missions.service";
import {MaskService} from "../../service/mask.service";
import {MatLegacyDialog as MatDialog} from "@angular/material/legacy-dialog";
import {
  MissionDeleteConfirmDialogComponent
} from "./mission-delete-confirm-dialog/mission-delete-confirm-dialog.component";
import {NotificationService} from "../../service/notification.service";
import {MissionModifyDialogComponent} from "./mission-modify-dialog/mission-modify-dialog.component";
import {Mission, MissionParam} from "../../model/mission.model";
import {FormControl} from "@angular/forms";
import {CdkDragDrop, moveItemInArray, transferArrayItem} from "@angular/cdk/drag-drop";
import {MatLegacySnackBar as MatSnackBar, MatLegacySnackBarRef as MatSnackBarRef} from "@angular/material/legacy-snack-bar";
import {MissionUploadService} from "./service/mission-upload.service";
import {MissionUploadSnackBarComponent} from "./mission-upload-snack-bar/mission-upload-snack-bar.component";

@Component({
  selector: 'app-missions',
  templateUrl: './missions.component.html',
  styleUrls: ['./missions.component.css']
})
export class MissionsComponent implements OnInit, OnDestroy {

  @ViewChild('uploadMission') uploadMissionComponent!: MissionUploadButtonComponent;

  disabledMissions: Mission[] = [];
  enabledMissions: Mission[] = [];
  filteredDisabledMissions: Mission[] = [];
  filteredEnabledMissions: Mission[] = [];

  reloadMissionsDataSubject: Subject<any>;
  reloadMissionDataSubscription!: Subscription;
  missionUploadSubscription!: Subscription;
  searchBoxControl!: FormControl;

  missionUploadSnackBarRef!: MatSnackBarRef<MissionUploadSnackBarComponent> | null;

  constructor(private missionsService: ServerMissionsService,
              private maskService: MaskService,
              private notificationService: NotificationService,
              private matDialog: MatDialog,
              private matSnackBar: MatSnackBar,
              private missionUploadService: MissionUploadService) {
    this.reloadMissionsDataSubject = new Subject();
    this.reloadMissionDataSubscription = this.reloadMissionsDataSubject.subscribe(() => {
      this.reloadMissions();
    });
    this.missionUploadSubscription = this.missionUploadService.missionUploadedSubject.subscribe((file) => {
      if (file) {
        this.reloadMissionsDataSubject.next(null);
      }
      if (this.missionUploadService.getUploadingMissions().length == 0) {
        this.missionUploadSnackBarRef?.dismiss();
        this.missionUploadSnackBarRef = null;
      }
    });
  }

  ngOnInit(): void {
    this.searchBoxControl = new FormControl('');
    this.searchBoxControl.valueChanges.subscribe(value => {
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
    this.showUploadProgressSnackBar();
  }

  showUploadProgressSnackBar() {
    if (!this.missionUploadSnackBarRef) {
      this.missionUploadSnackBarRef = this.matSnackBar.openFromComponent(MissionUploadSnackBarComponent);
      this.missionUploadSnackBarRef.afterDismissed().subscribe(() => {
        this.missionUploadSnackBarRef = null;
      });
    }
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
      data: mission
    });

    dialogRef.afterClosed().subscribe((savedParameters: MissionParam[]) => {
      if (savedParameters) {
        mission.parameters = savedParameters;
        this.save();
      }
    });
  }

  filterMissions(searchPhrase: string) {
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
