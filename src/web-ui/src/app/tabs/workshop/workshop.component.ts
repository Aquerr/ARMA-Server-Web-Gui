import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnDestroy, OnInit, signal } from "@angular/core";
import { WorkshopMod, WorkshopSortingType } from "../../model/workshop.model";
import { WorkshopService } from "../../service/workshop.service";
import { FormControl, ReactiveFormsModule } from "@angular/forms";
import { ModInstallWebsocketService } from "./mod-install-websocket/mod-install-websocket.service";
import { LoadingSpinnerMaskService } from "../../service/loading-spinner-mask.service";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatFormField, MatInput, MatLabel } from "@angular/material/input";
import { MatButton } from "@angular/material/button";
import { WorkshopItemComponent } from "./workshop-item/workshop-item.component";
import { MatCheckbox } from "@angular/material/checkbox";
import { MatOption, MatSelect } from "@angular/material/select";
import { SelectOption } from "../../model/control.model";

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
    WorkshopItemComponent,
    MatCheckbox,
    MatSelect,
    MatOption
  ],
  styleUrls: ["./workshop.component.scss"],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class WorkshopComponent implements OnInit, OnDestroy {
  workshopMods = signal<WorkshopMod[]>([]);
  installedWorkshopMods = signal<WorkshopMod[]>([]);
  modsUnderInstallation = signal<WorkshopMod[]>([]);
  nextCursor: string = "";
  searchBoxControl!: FormControl<string>;
  searchByModIdControl!: FormControl<boolean>;
  sortingControl!: FormControl<WorkshopSortingType>;
  daysPeriodControl!: FormControl<number>;

  public daysPeriodSelectOptions = signal<SelectOption<number>[]>([
    {
      value: 1,
      label: "Today"
    },
    {
      value: 7,
      label: "Week"
    },
    {
      value: 30,
      label: "Month"
    },
    {
      value: 90,
      label: "Three months"
    },
    {
      value: 365,
      label: "Year"
    },
    {
      value: -1,
      label: "All time"
    }
  ]);

  public sortingTypesSelectOptions = signal<SelectOption<string>[]>([
    {
      value: WorkshopSortingType.TEXT_RELEVANCE,
      label: "Search Relevance"
    },
    {
      value: WorkshopSortingType.POPULARITY,
      label: "Popularity"
    }, {
      value: WorkshopSortingType.MOST_SUBSCRIBERS,
      label: "Most subscribers"
    }, {
      value: WorkshopSortingType.LAST_UPDATED,
      label: "Last updated"
    }, {
      value: WorkshopSortingType.PUBLICATION_DATE,
      label: "Publication date"
    }]);

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
    this.searchBoxControl = new FormControl<string>("", { nonNullable: true });
    this.searchByModIdControl = new FormControl<boolean>(false, { nonNullable: true });
    this.sortingControl = new FormControl<WorkshopSortingType>(WorkshopSortingType.TEXT_RELEVANCE, { nonNullable: true });
    this.daysPeriodControl = new FormControl<number>(1, { nonNullable: true });
    this.modInstallWebsocketService.workShopModInstallStatus.subscribe((modInstallStatus) => {
      const workshopMod = this.workshopMods().find((mod) => mod.fileId === modInstallStatus.fileId);
      if (workshopMod) {
        workshopMod.progress = modInstallStatus.status;
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
      this.searchWorkshop("", this.searchBoxControl.value);
    }
  }

  nextPage() {
    this.searchWorkshop(this.nextCursor, this.lastSearchText);
  }

  searchWorkshop(cursor: string, searchText: string) {
    this.maskService.show();
    this.lastSearchText = searchText;
    this.workshopService
      .queryWorkshop({
        cursor: cursor,
        searchText: this.lastSearchText,
        searchByModId: this.searchByModIdControl.value,
        sortingType: this.sortingControl.value,
        daysPeriod: this.daysPeriodControl.value
      })
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
          progress: 0,
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
