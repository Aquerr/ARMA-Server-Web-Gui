import { Component, OnDestroy, OnInit, ViewChild } from "@angular/core";
import { Subject, Subscription } from "rxjs";
import { LoadingSpinnerMaskService } from "src/app/service/loading-spinner-mask.service";
import { ServerModsService } from "src/app/service/server-mods.service";
import { NotificationService } from "../../service/notification.service";
import { Mod } from "../../model/mod.model";
import { FormControl } from "@angular/forms";
import { ModUploadService } from "./service/mod-upload.service";
import { Router } from "@angular/router";
import { DialogService } from "../../service/dialog.service";
import { ModListsComponent } from "./mod-lists/mod-lists.component";
import { ModDownloadQueueDialogComponent } from "./mod-download-queue-dialog/mod-download-queue-dialog.component";
import { PermissionService } from "../../service/permission.service";
import { AswgAuthority } from "../../model/authority.model";

@Component({
  selector: "app-mods",
  templateUrl: "./mods.component.html",
  styleUrls: ["./mods.component.scss"],
  standalone: false
})
export class ModsComponent implements OnInit, OnDestroy {
  reloadModsDataSubject: Subject<any>;
  reloadModsDataSubscription!: Subscription;
  modUploadSubscription!: Subscription;

  searchBoxControl!: FormControl;
  isFileDragged: boolean = false;

  enabledMods: Mod[] = [];

  @ViewChild(ModListsComponent) modListsComponent!: ModListsComponent;

  constructor(
    private modService: ServerModsService,
    private maskService: LoadingSpinnerMaskService,
    private notificationService: NotificationService,
    private modUploadService: ModUploadService,
    private router: Router,
    private dialogService: DialogService,
    private permissionService: PermissionService
  ) {
    this.reloadModsDataSubject = new Subject();
    this.reloadModsDataSubscription = this.reloadModsDataSubject.subscribe(() => {
      this.modListsComponent.reloadMods();
      this.enabledMods = this.modListsComponent.enabledMods;
    });
    this.modUploadSubscription = this.modUploadService.fileUploadedSubject.subscribe((file) => {
      if (file) {
        this.reloadModsDataSubject.next(null);
      }
    });
  }

  ngOnInit(): void {
    this.searchBoxControl = new FormControl("");
    this.searchBoxControl.valueChanges.subscribe((value) => {
      this.modListsComponent.filterMods(value);
    });
  }

  ngOnDestroy(): void {
    this.reloadModsDataSubscription.unsubscribe();
    this.modUploadSubscription.unsubscribe();
  }

  onFileDropped(file: File) {
    this.maskService.show();
    this.modService.checkModFilesExists(file.name).subscribe((response) => {
      this.maskService.hide();
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
    this.modService
      .saveEnabledMods({ mods: this.modListsComponent.enabledMods })
      .subscribe((response) => {
        this.maskService.hide();
        this.notificationService.successNotification("Active mods list saved!", "Success");
      });
  }

  onModPresetSelected(presetName: string) {
    this.reloadModsDataSubject.next(null);
  }

  openModSettings() {
    this.router.navigate(["/mods-settings"]);
  }

  setFileDragged(isFileDragged: boolean) {
    this.isFileDragged = isFileDragged;
  }

  openDownloadQueueModal() {
    if (!this.permissionService.hasAllAuthorities([AswgAuthority.WORKSHOP_VIEW], true)) {
      return;
    }

    this.dialogService.open(ModDownloadQueueDialogComponent, (dialogResult) => {}, {}, {
      width: "70rem"
    });
  }
}
