import {
  ChangeDetectionStrategy,
  Component,
  computed,
  DestroyRef,
  inject,
  OnDestroy,
  OnInit,
  Signal,
  signal,
  ViewChild
} from "@angular/core";
import { debounceTime, Subject, Subscription } from "rxjs";
import { ServerMissionsService } from "@service/server-missions.service";
import { LoadingSpinnerMaskService } from "@service/loading-spinner-mask.service";
import { NotificationService } from "@service/notification.service";
import { Mission } from "@model/mission.model";
import { FormControl, ReactiveFormsModule } from "@angular/forms";
import { MissionUploadService } from "./service/mission-upload.service";
import { NewMissionDialogComponent } from "./new-mission-dialog/new-mission-dialog.component";
import { DialogService } from "@service/dialog.service";
import { DragAndDropFileDirective } from "@common-ui/directive/drag-and-drop-file.directive";
import { DragDropOverlay } from "@common-ui/drag-and-drop-overlay/drag-and-drop-overlay.component";
import { NgClass, NgTemplateOutlet } from "@angular/common";
import { MatButton } from "@angular/material/button";
import { MatFormField, MatInput, MatLabel } from "@angular/material/input";
import { MissionUploadButtonComponent } from "./upload-mission/mission-upload-button.component";
import { MissionListsComponent } from "./mission-lists/mission-lists.component";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";

@Component({
  selector: "app-missions",
  templateUrl: "./missions.component.html",
  imports: [
    DragAndDropFileDirective,
    DragDropOverlay,
    NgTemplateOutlet,
    NgClass,
    MatButton,
    MatFormField,
    MatLabel,
    ReactiveFormsModule,
    MatInput,
    MissionUploadButtonComponent,
    MissionListsComponent
  ],
  styleUrls: ["./missions.component.scss"],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MissionsComponent implements OnInit, OnDestroy {
  public searchPhrase = signal<string>("");
  public readonly enabledMissions: Signal<Mission[]>;

  @ViewChild(MissionListsComponent) missionListsComponent!: MissionListsComponent;

  private readonly missionUploadService = inject(MissionUploadService);
  private readonly missionsService = inject(ServerMissionsService);
  private readonly maskService = inject(LoadingSpinnerMaskService);
  private readonly notificationService = inject(NotificationService);
  private readonly dialogService = inject(DialogService);
  private readonly destroyRef = inject(DestroyRef);

  reloadMissionsDataSubject: Subject<void>;
  reloadMissionDataSubscription!: Subscription;
  missionUploadSubscription!: Subscription;
  searchBoxControl!: FormControl;
  isFileDragged: boolean = false;

  constructor() {
    this.enabledMissions = computed(() => this.missionListsComponent?.enabledMissions() ?? []);
    this.reloadMissionsDataSubject = new Subject();
    this.reloadMissionDataSubscription = this.reloadMissionsDataSubject.subscribe(() => {
      this.missionListsComponent.reloadMissions();
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
    this.searchBoxControl.valueChanges.pipe(takeUntilDestroyed(this.destroyRef), debounceTime(500)).subscribe((value) => {
      this.searchPhrase.set(value as string);
    });
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

  save() {
    this.maskService.show();
    this.missionsService
      .saveEnabledMissions({missionTemplates: this.enabledMissions().map((mission) => mission.template)})
      .subscribe(() => {
        this.maskService.hide();
        this.notificationService.successNotification("Active mission list saved!");
      });
  }

  addNewMission() {
    this.dialogService.open(NewMissionDialogComponent, (result: {
      file: File;
      template: string | undefined;
      name: string;
    }) => {
      if (result.file) {
        this.onFileDropped(result.file);
      } else if (result.template) {
        this.addBuiltInMission(result.name, result.template);
      }
    }, {}, {
      width: "450px",
      enterAnimationDuration: "200ms",
      exitAnimationDuration: "200ms"
    });
  }

  private addBuiltInMission(name: string, template: string) {
    this.maskService.show();
    this.missionsService.addTemplateMission(name, template).subscribe(() => {
      this.maskService.hide();
      this.notificationService.successNotification("Mission added!");
      this.reloadMissionsDataSubject.next();
    });
  }

  setFileDragged(isFileDragged: boolean) {
    this.isFileDragged = isFileDragged;
  }
}
