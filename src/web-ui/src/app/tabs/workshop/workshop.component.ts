import {Component, OnDestroy, OnInit, signal} from '@angular/core';
import {WorkshopMod} from '../../model/workshop.model';
import {WorkshopService} from '../../service/workshop.service';
import {FormControl} from '@angular/forms';
import {ModInstallWebsocketService} from "./mod-install-websocket/mod-install-websocket.service";
import {MaskService} from "../../service/mask.service";
import {PageEvent} from "@angular/material/paginator";

@Component({
  selector: 'app-workshop',
  templateUrl: './workshop.component.html',
  styleUrls: ['./workshop.component.scss']
})
export class WorkshopComponent implements OnInit, OnDestroy {
  workshopMods: WorkshopMod[] = [];
  installedWorkshopMods: WorkshopMod[] = [];
  modsUnderInstallation: WorkshopMod[] = [];
  nextCursor: string = "";
  searchBoxControl!: FormControl;
  private lastSearchText: string = "";

  // Paginator
  totalInstalledMods = signal(0);
  installedWorkshopModsToShow: WorkshopMod[] = [];

  constructor(private readonly workshopService: WorkshopService,
              private readonly maskService: MaskService,
              private readonly modInstallWebsocketService: ModInstallWebsocketService) {
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
      this.showInstalledWorkshopsModsPage(0, 10);
      this.totalInstalledMods.set(this.installedWorkshopMods.length);
    });
  }

  private showInstalledWorkshopsModsPage(pageIndex: number, pageSize: number) {
    const startIndex = pageIndex * pageSize;
    const endIndex = pageIndex * pageSize + pageSize;

    this.installedWorkshopModsToShow = this.installedWorkshopMods.slice(startIndex, endIndex);
  }

  changePage(event: PageEvent) {
    this.showInstalledWorkshopsModsPage(event.pageIndex, event.pageSize);
  }
}
