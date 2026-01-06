import { ChangeDetectorRef, Component, computed, OnDestroy, OnInit, signal } from "@angular/core";
import { Subject, Subscription } from "rxjs";
import { ServerMissionsService } from "../../service/server-missions.service";
import { LoadingSpinnerMaskService } from "../../service/loading-spinner-mask.service";
import { MatDialog } from "@angular/material/dialog";
import { MissionDeleteConfirmDialogComponent } from "./mission-delete-confirm-dialog/mission-delete-confirm-dialog.component";
import { NotificationService } from "../../service/notification.service";
import { MissionModifyDialogComponent } from "./mission-modify-dialog/mission-modify-dialog.component";
import { Mission } from "../../model/mission.model";
import { FormControl } from "@angular/forms";
import { CdkDragDrop, moveItemInArray, transferArrayItem } from "@angular/cdk/drag-drop";
import { MissionUploadService } from "./service/mission-upload.service";
import { NewMissionDialogComponent } from "./new-mission-dialog/new-mission-dialog.component";
import { DialogService } from "../../service/dialog.service";
import { moveItemBetweenSignalLists } from "../../util/signal/signal-utils";

@Component({
  selector: "app-missions",
  templateUrl: "./missions.component.html",
  styleUrls: ["./missions.component.scss"],
  standalone: false
})
export class MissionsComponent implements OnInit, OnDestroy {
  disabledMissions = signal<Mission[]>([]);
  enabledMissions = signal<Mission[]>([]);
  filteredDisabledMissions = computed<Mission[]>(() => {
    const missions = [...this.disabledMissions()];
    const searchPhrase = this.searchPhrase();
    if (searchPhrase == "") return missions;
    return missions.filter((mission) => {
      return mission.template.toLowerCase().includes(searchPhrase.toLowerCase());
    });
  });

  filteredEnabledMissions = computed<Mission[]>(() => {
    const missions = [...this.enabledMissions()];
    const searchPhrase = this.searchPhrase();
    if (searchPhrase == "") return missions;
    return missions.filter((mission) => {
      return mission.template.toLowerCase().includes(searchPhrase.toLowerCase());
    });
  });

  searchPhrase = signal<string>("");

  reloadMissionsDataSubject: Subject<void>;
  reloadMissionDataSubscription!: Subscription;
  missionUploadSubscription!: Subscription;
  searchBoxControl!: FormControl;
  isFileDragged: boolean = false;

  constructor(
    private missionsService: ServerMissionsService,
    private maskService: LoadingSpinnerMaskService,
    private notificationService: NotificationService,
    private matDialog: MatDialog,
    private missionUploadService: MissionUploadService,
    private dialogService: DialogService,
    private changeDetectorRef: ChangeDetectorRef
  ) {
    this.reloadMissionsDataSubject = new Subject();
    this.reloadMissionDataSubscription = this.reloadMissionsDataSubject.subscribe(() => {
      this.reloadMissions();
    });
    this.missionUploadSubscription = this.missionUploadService.fileUploadedSubject.subscribe(
      (file) => {
        if (file) {
          this.reloadMissionsDataSubject.next();
        }
      }
    );
  }

  ngOnInit(): void {
    this.searchBoxControl = new FormControl("");
    this.searchBoxControl.valueChanges.subscribe((value) => {
      this.filterMissions(value as string);
    });
    this.reloadMissions();
  }

  ngOnDestroy(): void {
    this.reloadMissionDataSubscription.unsubscribe();
    this.missionUploadSubscription.unsubscribe();
  }

  onFileDropped(file: File) {
    this.maskService.show();
    this.missionsService.checkMissionFileExists(file.name).subscribe((response) => {
      this.maskService.hide();
      if (response.exists) {
        const onCloseCallback = (result: boolean) => {
          if (!result) return;
          this.missionUploadService.uploadMission(file, true);
        };

        this.dialogService.openCommonConfirmationDialog(
          {
            question: `File for mission <strong>${file.name}</strong> already exists. <br>Do you want to overwrite it?`
          },
          onCloseCallback
        );
      } else {
        this.missionUploadService.uploadMission(file);
      }
    });
  }

  private reloadMissions(): void {
    this.maskService.show();
    this.missionsService.getInstalledMissions().subscribe((response) => {
      this.disabledMissions.set(response.disabledMissions);
      this.enabledMissions.set(response.enabledMissions);
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
    this.missionsService.deleteMission(missionTemplate).subscribe(() => {
      this.maskService.hide();
      this.notificationService.successNotification("Mission has been deleted!");
      this.reloadMissionsDataSubject.next();
    });
  }

  save() {
    this.maskService.show();
    this.missionsService
      .saveEnabledMissions({ missions: this.enabledMissions() })
      .subscribe(() => {
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
        this.changeDetectorRef.markForCheck();
      }
    });
  }

  filterMissions(searchPhrase: string) {
    this.searchPhrase.set(searchPhrase);
  }

  onMissionItemDropped(event: CdkDragDrop<Mission[]>) {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      const movedMission = event.previousContainer.data[event.previousIndex];
      if (event.previousContainer.id == "enabled-missions-list") {
        moveItemBetweenSignalLists(this.enabledMissions, this.disabledMissions, movedMission);
      } else {
        moveItemBetweenSignalLists(this.disabledMissions, this.enabledMissions, movedMission);
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

    dialogRef.afterClosed().subscribe((result: { file: File; template: string | undefined; name: string }) => {
      if (result.file) {
        this.onFileDropped(result.file);
      } else if (result.template) {
        this.addBuiltInMission(result.name, result.template);
      }
    });
  }

  private addBuiltInMission(name: string, template: string) {
    this.maskService.show();
    this.missionsService.addTemplateMission(name, template).subscribe(() => {
      this.maskService.hide();
      this.reloadMissions();
      this.notificationService.successNotification("Mission added!");
    });
  }

  private updateMission(mission: Mission) {
    this.maskService.show();
    this.missionsService.updateMission(mission.id, mission).subscribe(() => {
      this.maskService.hide();
      this.notificationService.successNotification("Mission updated!");
    });
  }

  getMissionNameForDisplay(mission: Mission) {
    return mission.name || mission.template;
  }

  setFileDragged(isFileDragged: boolean) {
    this.isFileDragged = isFileDragged;
  }
}
