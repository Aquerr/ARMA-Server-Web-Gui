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
import { Mod } from "../../../model/mod.model";
import { SaveEnabledModsRequest, ServerModsService } from "../../../service/server-mods.service";
import { NotificationService } from "../../../service/notification.service";
import { LoadingSpinnerMaskService } from "../../../service/loading-spinner-mask.service";
import { NotManagedModsComponent } from "../not-managed-mods/not-managed-mods.component";
import { Subject, Subscription } from "rxjs";
import { ModListComponent } from "../mod-list/mod-list.component";
import { moveItemBetweenSignalLists } from "../../../util/signal/signal-utils";

@Component({
  selector: "app-mod-lists",
  imports: [CdkDropListGroup, NotManagedModsComponent, ModListComponent],
  templateUrl: "./mod-lists.component.html",
  styleUrl: "./mod-lists.component.scss",
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ModListsComponent implements OnInit, OnDestroy {
  private readonly maskService: LoadingSpinnerMaskService = inject(LoadingSpinnerMaskService);
  private readonly notificationService: NotificationService = inject(NotificationService);
  private readonly modService: ServerModsService = inject(ServerModsService);

  searchPhrase = input<string>("");

  notManagedMods = signal<Mod[]>([]);
  disabledMods = signal<Mod[]>([]);
  enabledMods = signal<Mod[]>([]);

  reloadModsDataSubject!: Subject<void>;
  reloadModsDataSubscription!: Subscription;

  @ViewChildren(ModListComponent) modListComponents!: QueryList<ModListComponent>;

  constructor() {
    this.reloadModsDataSubject = new Subject();
    this.reloadModsDataSubscription = this.reloadModsDataSubject.subscribe(() => {
      this.reloadMods();
    });

    effect(() => {
      const searchPhrase = this.searchPhrase();
      this.modListComponents?.forEach((component) => component.filterMods(searchPhrase));
    });
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
      if (this.notManagedMods.length > 0) {
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
      this.modListComponents.forEach((component) => component.sortModList());
    }
  }

  onModDelete() {
    this.notificationService.successNotification("Mod has been deleted!");
    this.reloadModsDataSubject.next();
  }

  enableAllMods() {
    this.maskService.show();
    this.modService
      .saveEnabledMods({ mods: this.enabledMods().concat(this.disabledMods()) })
      .subscribe(() => {
        this.maskService.hide();
        this.reloadMods();
        this.notificationService.successNotification("Mods list updated!", "Success");
      });
  }

  disableAllMods() {
    this.maskService.show();
    this.modService
      .saveEnabledMods({ mods: [] } as SaveEnabledModsRequest)
      .subscribe(() => {
        this.maskService.hide();
        this.reloadMods();
        this.notificationService.successNotification("Mods list updated!", "Success");
      });
  }
}
