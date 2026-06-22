import {
  ChangeDetectionStrategy,
  Component,
  computed,
  DestroyRef,
  OnDestroy,
  OnInit,
  Signal,
  signal,
  ViewChild
} from "@angular/core";
import { debounceTime, finalize, Subject, Subscription } from "rxjs";
import { NotificationService } from "@service/notification.service";
import { Mod } from "@model/mod.model";
import { FormControl, ReactiveFormsModule } from "@angular/forms";
import { ModUploadService } from "./service/mod-upload.service";
import { DialogService } from "@service/dialog.service";
import { ModListsComponent } from "./mod-lists/mod-lists.component";
import { ModDownloadQueueDialogComponent } from "./mod-download-queue-dialog/mod-download-queue-dialog.component";
import { PermissionService } from "@service/permission.service";
import { AswgAuthority } from "@model/authority.model";
import { DragAndDropFileDirective } from "@common-ui/directive/drag-and-drop-file.directive";
import { DragDropOverlay } from "@common-ui/drag-and-drop-overlay/drag-and-drop-overlay.component";
import { NgClass, NgTemplateOutlet } from "@angular/common";
import { MatFormField, MatInput, MatLabel } from "@angular/material/input";
import { MatButton } from "@angular/material/button";
import { RouterLink } from "@angular/router";
import { ModPresetsComponent } from "./mod-presets/mod-presets.component";
import { ModUploadButtonComponent } from "./mod-upload-button/mod-upload-button.component";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { ServerModsService } from "@service/server-mods.service";
import { LoadingSpinnerMaskService } from "@service/loading-spinner-mask.service";

@Component({
  selector: "app-mods",
  templateUrl: "./mods.component.html",
  imports: [
    DragAndDropFileDirective,
    DragDropOverlay,
    NgTemplateOutlet,
    NgClass,
    MatFormField,
    MatLabel,
    MatInput,
    ReactiveFormsModule,
    MatButton,
    RouterLink,
    ModPresetsComponent,
    ModListsComponent,
    ModUploadButtonComponent
  ],
  styleUrls: ["./mods.component.scss"],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ModsComponent implements OnInit, OnDestroy {
  reloadModsDataSubject: Subject<void>;
  reloadModsDataSubscription!: Subscription;
  modUploadSubscription!: Subscription;

  searchBoxControl!: FormControl;
  searchPhrase = signal<string>("");
  isFileDragged: boolean = false;

  enabledMods: Signal<Mod[]>;

  @ViewChild(ModListsComponent) modListsComponent!: ModListsComponent;

  constructor(
    private readonly modsService: ServerModsService,
    private readonly maskService: LoadingSpinnerMaskService,
    private readonly notificationService: NotificationService,
    private readonly modUploadService: ModUploadService,
    private readonly dialogService: DialogService,
    private readonly permissionService: PermissionService,
    private readonly destroyRef: DestroyRef
  ) {
    this.enabledMods = computed(() => this.modListsComponent?.enabledMods() ?? []);
    this.reloadModsDataSubject = new Subject();
    this.reloadModsDataSubscription = this.reloadModsDataSubject.subscribe(() => {
      this.modListsComponent.reloadMods();
    });
    this.modUploadSubscription = this.modUploadService.fileUploadedSubject.subscribe((file) => {
      if (file) {
        this.reloadModsDataSubject.next();
      }
    });
  }

  ngOnInit(): void {
    this.searchBoxControl = new FormControl("");
    this.searchBoxControl.valueChanges.pipe(takeUntilDestroyed(this.destroyRef), debounceTime(500)).subscribe(
      (value: string) => {
        this.searchPhrase.set(value ?? "");
      });
  }

  ngOnDestroy(): void {
    this.reloadModsDataSubscription.unsubscribe();
    this.modUploadSubscription.unsubscribe();
  }

  onFileDropped(file: File) {
    this.maskService.show();
    this.modsService.checkModFilesExists(file.name).pipe(finalize(() => this.maskService.hide())).subscribe((response) => {
      if (response.exists) {
        const onCloseCallback = (result: boolean) => {
          if (!result) return;
          this.modUploadService.uploadMod(file, true);
        };

        this.dialogService.openCommonConfirmationDialog(
          {
            question: `File for mod <strong>${file.name}</strong> already exists. <br>Do you want to overwrite it?`
          },
          onCloseCallback
        );
      } else {
        this.modUploadService.uploadMod(file);
      }
    });
  }

  save() {
    this.maskService.show();
    this.enabledMods = this.modListsComponent.enabledMods;
    this.modsService
      .saveEnabledMods({ mods: this.modListsComponent.enabledMods() })
      .pipe(finalize(() => this.maskService.hide()))
      .subscribe(() => {
        this.notificationService.successNotification("Active mods list saved!");
      });
  }

  onModPresetSelected(presetName: string) {
    this.maskService.show();
    this.modsService.selectPreset({ name: presetName }).pipe(finalize(() => this.maskService.hide())).subscribe(() => {
      this.notificationService.successNotification("Mod preset loaded!");
      this.reloadModsDataSubject.next();
    });
  }

  setFileDragged(isFileDragged: boolean) {
    this.isFileDragged = isFileDragged;
  }

  openDownloadQueueModal() {
    if (!this.permissionService.hasAllAuthorities([AswgAuthority.WORKSHOP_VIEW], true)) {
      return;
    }

    this.dialogService.open(ModDownloadQueueDialogComponent, () => undefined, {}, {
      width: "70rem"
    });
  }
}
