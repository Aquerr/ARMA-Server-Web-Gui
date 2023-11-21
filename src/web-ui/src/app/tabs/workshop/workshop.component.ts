import {Component, OnDestroy, OnInit} from '@angular/core';
import {WorkshopMod} from '../../model/workshop.model';
import {WorkshopService} from '../../service/workshop.service';
import {FormControl} from '@angular/forms';
import {ModInstallWebsocketService} from "./mod-install-websocket/mod-install-websocket.service";
import {MaskService} from "../../service/mask.service";

@Component({
  selector: 'app-workshop',
  templateUrl: './workshop.component.html',
  styleUrls: ['./workshop.component.css']
})
export class WorkshopComponent implements OnInit, OnDestroy {
  workshopMods: WorkshopMod[] = [];
  installedWorkshopMods: WorkshopMod[] = [];
  modsUnderInstallation: WorkshopMod[] = [];

  nextCursor: string = "";
  searchBoxControl!: FormControl;
  private lastSearchText: string = "";

  constructor(private workshopService: WorkshopService,
              private maskService: MaskService,
              private modInstallWebsocketService: ModInstallWebsocketService) {
    this.searchBoxControl = new FormControl<string>('');
    this.modInstallWebsocketService.workShopModInstallStatus.subscribe(modInstallStatus => {
      const workshopMod = this.workshopMods.find(mod => mod.fileId === modInstallStatus.fileId);
      if (workshopMod) {
        workshopMod.isBeingInstalled = modInstallStatus.status != 100;
      }
    });
  }

  ngOnInit(): void {
    this.reloadInstalledModList();
    this.modInstallWebsocketService.connect();
  }

  ngOnDestroy(): void {
    this.modInstallWebsocketService.disconnect();
  }

  onSearchBoxKeyDown($event: KeyboardEvent) {
    if ($event.code === 'Enter' || $event.code === 'NumpadEnter') {
      this.searchWorkshop('', this.searchBoxControl.value)
    }
  }
  nextPage() {
    this.searchWorkshop(this.nextCursor, this.lastSearchText);
  }

  searchWorkshop(cursor: string, searchText: string) {
    this.maskService.show();
    this.lastSearchText = searchText;
    this.workshopService.queryWorkshop({cursor: cursor, searchText: this.lastSearchText}).subscribe(response => {
      this.nextCursor = response.nextCursor;
      this.workshopMods = response.mods.map(mod => {
        if (this.modsUnderInstallation.find(modUnderInstallation => modUnderInstallation.fileId === mod.fileId) !== undefined) {
          mod.isBeingInstalled = true;
        }
        return mod;
      });
      this.maskService.hide();
    });
  }

  canInstall(workshopMod: WorkshopMod) {
    return this.installedWorkshopMods.find(mod => mod.fileId === workshopMod.fileId) === undefined;
  }

  onModInstallDelete($event: any) {
    this.reloadInstalledModList();
  }

  private reloadInstalledModList() {
    this.workshopService.getInstalledWorkshopItems().subscribe(response => {
      this.installedWorkshopMods = response.mods;
      this.modsUnderInstallation = response.modsUnderInstallation.map(request => {
        return {
          fileId: request.fileId,
          title: request.modName,
          isBeingInstalled: true
        } as WorkshopMod
      });

      this.installedWorkshopMods.push(...this.modsUnderInstallation);
    });
  }
}
