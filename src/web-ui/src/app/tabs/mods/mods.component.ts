import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Subject, Subscription} from 'rxjs';
import { MaskService } from 'src/app/service/mask.service';
import { ServerModsService } from 'src/app/service/server-mods.service';
import { UploadModComponent } from './upload-mod/upload-mod.component';
import {MatDialog} from "@angular/material/dialog";
import {ModDeleteConfirmDialogComponent} from "./mod-delete-confirm-dialog/mod-delete-confirm-dialog.component";

@Component({
  selector: 'app-mods',
  templateUrl: './mods.component.html',
  styleUrls: ['./mods.component.css']
})
export class ModsComponent implements OnInit, OnDestroy {

  @ViewChild('uploadMod') uploadModComponent!: UploadModComponent;

  reloadModsDataSubject: Subject<any>;
  reloadModsDataSubscription!: Subscription;

  mods: string[] = [];

  constructor(private modService: ServerModsService, private maskService: MaskService, private matDialog: MatDialog) {
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
      this.mods = modsResponse.mods;
      this.maskService.hide();
    });
  }
}
