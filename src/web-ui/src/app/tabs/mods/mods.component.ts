import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Subject, Subscription} from 'rxjs';
import { MaskService } from 'src/app/service/mask.service';
import { ServerModsService } from 'src/app/service/server-mods.service';
import { UploadModComponent } from './upload-mod/upload-mod.component';
import {MatDialog} from "@angular/material/dialog";
import {ModDeleteConfirmDialogComponent} from "./mod-delete-confirm-dialog/mod-delete-confirm-dialog.component";
import {AswgDragDropListComponent} from "../../common-ui/aswg-drag-drop-list/aswg-drag-drop-list.component";
import {NotificationService} from "../../service/notification.service";

@Component({
  selector: 'app-mods',
  templateUrl: './mods.component.html',
  styleUrls: ['./mods.component.css']
})
export class ModsComponent implements OnInit, OnDestroy {

  @ViewChild('uploadMod') uploadModComponent!: UploadModComponent;
  @ViewChild('enabledModsList') enabledModsList!: AswgDragDropListComponent;

  reloadModsDataSubject: Subject<any>;
  reloadModsDataSubscription!: Subscription;

  disabledMods: string[] = [];
  enabledMods: string[] = [];

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
      this.maskService.hide();
    });
  }

  save() {
    this.maskService.show();
    console.log(this.enabledModsList.items);
    this.modService.saveEnabledMods({mods: this.enabledModsList.items}).subscribe(response => {
      this.maskService.hide();
      this.notificationService.successNotification('Active mods list saved!', 'Success');
    });
  }
}
