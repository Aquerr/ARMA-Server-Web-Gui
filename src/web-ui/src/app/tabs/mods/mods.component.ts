import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Subject, Subscription} from 'rxjs';
import { MaskService } from 'src/app/service/mask.service';
import { ServerModsService } from 'src/app/service/server-mods.service';
import { UploadModComponent } from './upload-mod/upload-mod.component';
import {MatDialog} from "@angular/material/dialog";
import {ModDeleteConfirmDialogComponent} from "./mod-delete-confirm-dialog/mod-delete-confirm-dialog.component";
import {NotificationService} from "../../service/notification.service";
import {Mod} from "../../model/mod.model";
import {ModsListComponent} from "./mods-list/mods-list.component";

@Component({
  selector: 'app-mods',
  templateUrl: './mods.component.html',
  styleUrls: ['./mods.component.css']
})
export class ModsComponent implements OnInit, OnDestroy {

  @ViewChild('uploadMod') uploadModComponent!: UploadModComponent;
  @ViewChild('enabledModsList') enabledModsList!: ModsListComponent;

  reloadModsDataSubject: Subject<any>;
  reloadModsDataSubscription!: Subscription;

  disabledMods: Mod[] = [];
  enabledMods: Mod[] = [];

  constructor(private modService: ServerModsService,
              private maskService: MaskService,
              private matDialog: MatDialog,
              private notificationService: NotificationService) {
    this.reloadModsDataSubject = new Subject();
  }

  ngOnInit(): void {
    this.reloadMods();
    this.reloadModsDataSubscription = this.reloadModsDataSubject.subscribe(() => {
      this.reloadMods();
    });
  }

  ngOnDestroy(): void {
    this.reloadModsDataSubscription.unsubscribe();
  }

  onModUploaded() {
    this.reloadModsDataSubject.next(null);
  }

  showModDeleteConfirmationDialog(modName: string) {
    const dialogRef = this.matDialog.open(ModDeleteConfirmDialogComponent, {
      width: '250px',
      enterAnimationDuration: '200ms',
      exitAnimationDuration: '200ms'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.deleteMod(modName);
      }
    });
  }

  deleteMod(modName: string) {
    this.maskService.show();
    this.modService.deleteMod(modName).subscribe(response => {
      this.maskService.hide();
      this.reloadModsDataSubject.next(null);
    });
  }

  onFileDropped(file: File) {
    this.uploadModComponent.uploadFile(file);
  }

  private reloadMods() {
    this.maskService.show();
    this.modService.getInstalledMods().subscribe(modsResponse => {
      this.disabledMods = modsResponse.disabledMods;
      this.enabledMods = modsResponse.enabledMods;
      console.log(modsResponse);
      this.maskService.hide();
    });
  }

  save() {
    this.maskService.show();
    console.log(this.enabledModsList.mods);
    this.modService.saveEnabledMods({mods: this.enabledModsList.mods}).subscribe(response => {
      this.maskService.hide();
      this.notificationService.successNotification('Active mods list saved!', 'Success');
    });
  }
}
