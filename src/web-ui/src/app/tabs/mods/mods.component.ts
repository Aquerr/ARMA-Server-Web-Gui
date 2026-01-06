import { AfterViewInit, Component, OnDestroy, OnInit, signal, ViewChild } from "@angular/core";
import { Subject, Subscription } from "rxjs";
import { LoadingSpinnerMaskService } from "src/app/service/loading-spinner-mask.service";
import { ServerModsService } from "src/app/service/server-mods.service";
import { NotificationService } from "../../service/notification.service";
import { Mod } from "../../model/mod.model";
import { FormControl } from "@angular/forms";
import { ModUploadService } from "./service/mod-upload.service";
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
export class ModsComponent implements OnInit, OnDestroy, AfterViewInit {
  reloadModsDataSubject: Subject<void>;
  reloadModsDataSubscription!: Subscription;
  modUploadSubscription!: Subscription;

  searchBoxControl!: FormControl;
  searchPhrase = signal<string>("");
  isFileDragged: boolean = false;

  enabledMods = signal<Mod[]>([]);

  @ViewChild(ModListsComponent) modListsComponent!: ModListsComponent;

  constructor(
    private modService: ServerModsService,
    private maskService: LoadingSpinnerMaskService,
    private notificationService: NotificationService,
    private modUploadService: ModUploadService,
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
        this.reloadModsDataSubject.next();
      }
    });
  }

  ngOnInit(): void {
    this.searchBoxControl = new FormControl("");
    this.searchBoxControl.valueChanges.subscribe((value) => {
      this.searchPhrase.set(value as string);
    });
  }

  ngAfterViewInit() {
    this.enabledMods = this.modListsComponent.enabledMods;
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
      .saveEnabledMods({ mods: this.modListsComponent.enabledMods() })
      .subscribe(() => {
        this.maskService.hide();
        this.notificationService.successNotification("Active mods list saved!", "Success");
      });
  }

  onModPresetSelected() {
    this.reloadModsDataSubject.next();
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
