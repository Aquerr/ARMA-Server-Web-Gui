import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Subject, Subscription} from 'rxjs';
import { MaskService } from 'src/app/service/mask.service';
import { ServerModsService } from 'src/app/service/server-mods.service';
import { ModUploadButtonComponent } from './mod-upload-button/mod-upload-button.component';
import {MatDialog} from "@angular/material/dialog";
import {ModDeleteConfirmDialogComponent} from "./mod-delete-confirm-dialog/mod-delete-confirm-dialog.component";
import {NotificationService} from "../../service/notification.service";
import {Mod} from "../../model/mod.model";
import {FormControl} from "@angular/forms";
import {CdkDragDrop, moveItemInArray, transferArrayItem} from "@angular/cdk/drag-drop";
import {MatSnackBar, MatSnackBarRef} from "@angular/material/snack-bar";
import {ModUploadSnackBarComponent} from "./mod-upload-snack-bar/mod-upload-snack-bar.component";
import {ModUploadService} from "./service/mod-upload.service";

@Component({
  selector: 'app-mods',
  templateUrl: './mods.component.html',
  styleUrls: ['./mods.component.css']
})
export class ModsComponent implements OnInit, OnDestroy {

  @ViewChild('uploadMod') uploadModComponent!: ModUploadButtonComponent;

  reloadModsDataSubject: Subject<any>;
  reloadModsDataSubscription!: Subscription;

  disabledMods: Mod[] = [];
  enabledMods: Mod[] = [];
  filteredDisabledMods: Mod[] = [];
  filteredEnabledMods: Mod[] = [];

  searchBoxControl!: FormControl;

  modUploadSnackBarRef!: MatSnackBarRef<ModUploadSnackBarComponent> | null;

  constructor(private modService: ServerModsService,
              private maskService: MaskService,
              private matDialog: MatDialog,
              private notificationService: NotificationService,
              private matSnackBar: MatSnackBar,
              private modUploadService: ModUploadService) {
    this.reloadModsDataSubject = new Subject();
  }

  ngOnInit(): void {
    this.searchBoxControl = new FormControl('');
    this.searchBoxControl.valueChanges.subscribe(value => {
      this.filterMods(value);
    });
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
    this.modUploadService.uploadMod(file);
    this.openUploadSnackBar();
  }

  openUploadSnackBar() {
    if (!this.modUploadSnackBarRef) {
      this.modUploadSnackBarRef = this.matSnackBar.openFromComponent(ModUploadSnackBarComponent);
      this.modUploadSnackBarRef.afterDismissed().subscribe(() => {
        this.modUploadSnackBarRef = null;
      });
    }
  }

  private reloadMods() {
    this.maskService.show();
    this.modService.getInstalledMods().subscribe(modsResponse => {
      this.disabledMods = modsResponse.disabledMods;
      this.enabledMods = modsResponse.enabledMods;
      this.filteredDisabledMods = [...this.disabledMods];
      this.filteredEnabledMods = [...this.enabledMods];
      this.maskService.hide();
    });
  }

  save() {
    this.maskService.show();
    console.log(this.enabledMods);
    this.modService.saveEnabledMods({mods: this.enabledMods}).subscribe(response => {
      this.maskService.hide();
      this.notificationService.successNotification('Active mods list saved!', 'Success');
    });
  }

  private filterMods(searchPhrase: string) {
    this.filteredEnabledMods = this.enabledMods.filter(mod => mod.name.toLowerCase().includes(searchPhrase.toLowerCase()));
    this.filteredDisabledMods = this.disabledMods.filter(mod => mod.name.toLowerCase().includes(searchPhrase.toLowerCase()));
  }

  onModItemDragDrop(event: CdkDragDrop<Mod[]>) {
    if (event.previousContainer === event.container){
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      let movedMod = event.previousContainer.data[event.previousIndex];
      if (event.previousContainer.id == 'enabled-mods-list') {
        this.enabledMods.forEach((value, index) => {
          if (value == movedMod) this.enabledMods.splice(index, 1);
        });
        this.disabledMods.push(movedMod);
      } else {
        this.disabledMods.forEach((value, index) => {
          if (value == movedMod) this.disabledMods.splice(index, 1);
        });
        this.enabledMods.push(movedMod);
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
