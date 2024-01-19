import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subject, Subscription} from 'rxjs';
import { MaskService } from 'src/app/service/mask.service';
import {SaveEnabledModsRequest, ServerModsService} from 'src/app/service/server-mods.service';
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

  reloadModsDataSubject: Subject<any>;
  reloadModsDataSubscription!: Subscription;
  modUploadSubscription!: Subscription;

  disabledMods: Mod[] = [];
  enabledMods: Mod[] = [];
  filteredDisabledMods: Mod[] = [];
  filteredEnabledMods: Mod[] = [];

  searchBoxControl!: FormControl;

  modUploadSnackBarRef!: MatSnackBarRef<ModUploadSnackBarComponent> | null;

  constructor(private modService: ServerModsService,
              private maskService: MaskService,
              private notificationService: NotificationService,
              private matSnackBar: MatSnackBar,
              private modUploadService: ModUploadService) {

    this.reloadModsDataSubject = new Subject();
    this.reloadModsDataSubscription = this.reloadModsDataSubject.subscribe(() => {
      this.reloadMods();
    });
    this.modUploadSubscription = this.modUploadService.modUploadedSubject.subscribe((file) => {
      if (file) {
        this.reloadModsDataSubject.next(null);
      }
      if (!this.modUploadService.getUploadingMods().length) {
        this.modUploadSnackBarRef?.dismiss();
        this.modUploadSnackBarRef = null;
      }
    });
  }

  ngOnInit(): void {
    this.searchBoxControl = new FormControl('');
    this.searchBoxControl.valueChanges.subscribe(value => {
      this.filterMods(value);
    });
    this.reloadMods();
  }

  ngOnDestroy(): void {
    this.reloadModsDataSubscription.unsubscribe();
    this.modUploadSubscription.unsubscribe();
  }

  onFileDropped(file: File) {
    this.modUploadService.uploadMod(file);
    this.showUploadProgressSnackBar();
  }

  showUploadProgressSnackBar() {
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
      this.filteredDisabledMods = [...this.disabledMods].sort((a, b) => a.name.localeCompare(b.name));
      this.filteredEnabledMods = [...this.enabledMods].sort((a, b) => a.name.localeCompare(b.name));
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
    this.sortModList(this.filteredEnabledMods);
    this.sortModList(this.filteredDisabledMods);
  }

  onModItemDragDrop(event: CdkDragDrop<Mod[]>) {
    if (event.previousContainer === event.container){
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      let movedMod = event.previousContainer.data[event.previousIndex];
      // let currentIndex: number;
      if (event.previousContainer.id == 'enabled-mods-list') {
        this.removeModFromList(this.enabledMods, movedMod);
        this.disabledMods.push(movedMod);
      } else {
        this.removeModFromList(this.disabledMods, movedMod);
        this.enabledMods.push(movedMod);
      }

      // Update view drag drop list
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex,
      );

      this.sortModList(this.filteredDisabledMods);
      this.sortModList(this.filteredEnabledMods);
    }
  }

  private removeModFromList(list: Mod[], mod: Mod) {
    list.forEach((value, index) => {
      if (value == mod) list.splice(index, 1);
    });
  }

  private sortModList(list: Mod[]) {
    list.sort((a, b) => a.name.localeCompare(b.name));
  }

  onModDelete(mod: Mod) {
    this.reloadModsDataSubject.next(null);
  }

  onModPresetSelected(presetName: string) {
    console.log("Reload mod list...");
    this.reloadModsDataSubject.next(null);
  }

  enableAllMods() {
    this.maskService.show();
    this.modService.saveEnabledMods({mods: this.enabledMods.concat(this.disabledMods)}).subscribe(response => {
      this.maskService.hide();
      this.reloadMods();
      this.notificationService.successNotification('Mods list updated!', 'Success');
    });
  }

  disableAllMods() {
    this.maskService.show();
    this.modService.saveEnabledMods({} as SaveEnabledModsRequest).subscribe(response => {
      this.maskService.hide();
      this.reloadMods();
      this.notificationService.successNotification('Mods list updated!', 'Success');
    });
  }
}
