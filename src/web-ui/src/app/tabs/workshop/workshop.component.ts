import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnDestroy, OnInit, signal } from "@angular/core";
import { WorkshopMod } from "../../model/workshop.model";
import { WorkshopService } from "../../service/workshop.service";
import { FormControl, ReactiveFormsModule } from "@angular/forms";
import { ModInstallWebsocketService } from "./mod-install-websocket/mod-install-websocket.service";
import { LoadingSpinnerMaskService } from "../../service/loading-spinner-mask.service";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatFormField, MatInput, MatLabel } from "@angular/material/input";
import { MatButton } from "@angular/material/button";
import { WorkshopItemComponent } from "./workshop-item/workshop-item.component";

@Component({
  selector: "app-workshop",
  templateUrl: "./workshop.component.html",
  imports: [
    MatFormField,
    MatLabel,
    ReactiveFormsModule,
    MatButton,
    MatPaginator,
    MatInput,
    WorkshopItemComponent
  ],
  styleUrls: ["./workshop.component.scss"],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class WorkshopComponent implements OnInit, OnDestroy {
  workshopMods = signal<WorkshopMod[]>([]);
  installedWorkshopMods = signal<WorkshopMod[]>([]);
  modsUnderInstallation = signal<WorkshopMod[]>([]);
  nextCursor: string = "";
  searchBoxControl!: FormControl;
  private lastSearchText: string = "";

  private changeDetectorRef = inject(ChangeDetectorRef);

  // Paginator
  totalInstalledMods = signal(0);
  installedWorkshopModsToShow: WorkshopMod[] = [];

  constructor(
    private readonly workshopService: WorkshopService,
    private readonly maskService: LoadingSpinnerMaskService,
    private readonly modInstallWebsocketService: ModInstallWebsocketService
  ) {
    this.searchBoxControl = new FormControl<string>("");
    this.modInstallWebsocketService.workShopModInstallStatus.subscribe((modInstallStatus) => {
      const workshopMod = this.workshopMods().find((mod) => mod.fileId === modInstallStatus.fileId);
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
    if ($event.code === "Enter" || $event.code === "NumpadEnter") {
      this.searchWorkshop("", this.searchBoxControl.value as string);
    }
  }

  nextPage() {
    this.searchWorkshop(this.nextCursor, this.lastSearchText);
  }

  searchWorkshop(cursor: string, searchText: string) {
    this.maskService.show();
    this.lastSearchText = searchText;
    this.workshopService
      .queryWorkshop({ cursor: cursor, searchText: this.lastSearchText })
      .subscribe((response) => {
        this.nextCursor = response.nextCursor;

        this.workshopMods.set(response.mods.map((mod) => {
          if (
            this.modsUnderInstallation().find(
              (modUnderInstallation) => modUnderInstallation.fileId === mod.fileId
            ) !== undefined
          ) {
            mod.isBeingInstalled = true;
          }
          return mod;
        }));
        this.maskService.hide();
      });
  }

  canInstall(workshopMod: WorkshopMod) {
    return (
      this.installedWorkshopMods().find((mod) => mod.fileId === workshopMod.fileId) === undefined
    );
  }

  onModInstallDelete() {
    this.reloadInstalledModList();
  }

  private reloadInstalledModList() {
    this.workshopService.getInstalledWorkshopItems().subscribe((response) => {
      this.installedWorkshopMods.set(response.mods);
      this.modsUnderInstallation.set(response.modsUnderInstallation.map((request) => {
        return {
          fileId: request.fileId,
          title: request.modName,
          isBeingInstalled: true
        } as WorkshopMod;
      }));

      this.installedWorkshopMods.update((mods) => [...mods, ...this.modsUnderInstallation()]);
      this.showInstalledWorkshopsModsPage(0, 10);
      this.totalInstalledMods.set(this.installedWorkshopMods().length);
    });
  }

  private showInstalledWorkshopsModsPage(pageIndex: number, pageSize: number) {
    const startIndex = pageIndex * pageSize;
    const endIndex = pageIndex * pageSize + pageSize;

    this.installedWorkshopModsToShow = this.installedWorkshopMods().slice(startIndex, endIndex);
    this.changeDetectorRef.markForCheck();
  }

  changePage(event: PageEvent) {
    this.showInstalledWorkshopsModsPage(event.pageIndex, event.pageSize);
  }
}
