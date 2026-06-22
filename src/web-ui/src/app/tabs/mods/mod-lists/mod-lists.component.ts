import {
  ChangeDetectionStrategy,
  Component,
  effect,
  inject,
  input,
  OnDestroy,
  OnInit,
  QueryList,
  signal,
  ViewChildren
} from "@angular/core";
import { CdkDragDrop, CdkDropListGroup, moveItemInArray, transferArrayItem } from "@angular/cdk/drag-drop";
import { Mod } from "@model/mod.model";
import { ServerModsService } from "@service/server-mods.service";
import { NotificationService } from "@service/notification.service";
import { LoadingSpinnerMaskService } from "@service/loading-spinner-mask.service";
import { NotManagedModsComponent } from "../not-managed-mods/not-managed-mods.component";
import { Subject, Subscription } from "rxjs";
import { moveItemBetweenSignalLists } from "@app/util/signal/signal-utils";
import { ModListItemComponent } from "../mod-list-item/mod-list-item.component";
import {
  AswgDragAndDropListComponent,
  SortOption
} from "@common-ui/aswg-drag-and-drop-list/aswg-drag-and-drop-list.component";

@Component({
  selector: "app-mod-lists",
  imports: [CdkDropListGroup, NotManagedModsComponent, ModListItemComponent, AswgDragAndDropListComponent],
  templateUrl: "./mod-lists.component.html",

  styleUrl: "./mod-lists.component.scss",
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ModListsComponent implements OnInit, OnDestroy {
  private readonly maskService: LoadingSpinnerMaskService = inject(LoadingSpinnerMaskService);
  private readonly notificationService: NotificationService = inject(NotificationService);
  private readonly modService: ServerModsService = inject(ServerModsService);

  public readonly searchPhrase = input<string>("");

  public readonly notManagedMods = signal<Mod[]>([]);
  public readonly disabledMods = signal<Mod[]>([]);
  public readonly enabledMods = signal<Mod[]>([]);

  public sortingOptions: SortOption<Mod>[] = [
    {
      value: "Name_Asc",
      label: "Name",
      icon: "arrow_downward_alt",
      sortFunction: (a, b) => a.name.localeCompare(b.name)
    },
    {
      value: "Name_Desc",
      label: "Name",
      icon: "arrow_upward_alt",
      sortFunction: (a, b) => b.name.localeCompare(a.name)
    },
    {
      value: "Size_Asc",
      label: "Size",
      icon: "arrow_downward_alt",
      sortFunction: (a, b) => a.sizeBytes - b.sizeBytes
    },
    {
      value: "Size_Desc",
      label: "Size",
      icon: "arrow_upward_alt",
      sortFunction: (a, b) => b.sizeBytes - a.sizeBytes
    }
  ];

  private reloadModsDataSubject!: Subject<void>;
  private reloadModsDataSubscription!: Subscription;

  @ViewChildren(AswgDragAndDropListComponent<Mod>) modListComponents!: QueryList<AswgDragAndDropListComponent<Mod>>;

  constructor() {
    this.reloadModsDataSubject = new Subject();
    this.reloadModsDataSubscription = this.reloadModsDataSubject.subscribe(() => {
      this.reloadMods();
    });

    effect(() => {
      const searchPhrase = this.searchPhrase();
      this.modListComponents?.forEach((component) => component.filterItems(searchPhrase));
    });
  }

  protected modFilterFunction(mod: Mod, searchPhrase: string): boolean {
    return mod.name.toLowerCase().includes(searchPhrase.toLowerCase());
  }

  ngOnInit() {
    this.reloadMods();
  }

  ngOnDestroy(): void {
    this.reloadModsDataSubscription.unsubscribe();
  }

  public reloadMods() {
    this.maskService.show();
    this.modService.getInstalledMods().subscribe((modsResponse) => {
      this.notManagedMods.set(modsResponse.notManagedMods);
      this.disabledMods.set(modsResponse.disabledMods);
      this.enabledMods.set(modsResponse.enabledMods);
      this.modListComponents.forEach((component) => component.reload());

      this.maskService.hide();
      if (this.notManagedMods().length > 0) {
        this.notificationService.infoNotification(
          "ASWG detected some new mods. Scroll to the bottom to see them."
        );
      }
    });
  }

  onModItemDragDrop(event: CdkDragDrop<Mod[], Mod[], Mod>) {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      const movedMod = event.previousContainer.data[event.previousIndex];
      if (event.previousContainer.id == "Enabled") {
        moveItemBetweenSignalLists(this.enabledMods, this.disabledMods, movedMod);
      } else {
        moveItemBetweenSignalLists(this.disabledMods, this.enabledMods, movedMod);
      }

      // Update view drag drop list
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex
      );
      this.modListComponents.forEach((component) => component.sortItems());
    }
  }

  onModDelete(mod: Mod) {
    this.modService.deleteMod(mod.name).subscribe(() => {
      this.notificationService.successNotification("Mod has been deleted!");
      this.reloadModsDataSubject.next();
      this.maskService.hide();
    });
  }

  enableAllMods() {
    this.maskService.show();
    this.modService
      .saveEnabledMods({ mods: this.enabledMods().concat(this.disabledMods()) })
      .subscribe(() => {
        this.maskService.hide();
        this.reloadMods();
        this.notificationService.successNotification("Mods list updated!");
      });
  }

  disableAllMods() {
    this.maskService.show();
    this.modService
      .saveEnabledMods({ mods: [] })
      .subscribe(() => {
        this.maskService.hide();
        this.reloadMods();
        this.notificationService.successNotification("Mods list updated!");
      });
  }
}
