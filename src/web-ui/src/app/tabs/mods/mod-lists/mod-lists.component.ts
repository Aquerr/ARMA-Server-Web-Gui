import { Component, inject, OnDestroy, OnInit, QueryList, ViewChildren } from "@angular/core";
import {
  CdkDragDrop,
  CdkDropListGroup,
  moveItemInArray,
  transferArrayItem
} from "@angular/cdk/drag-drop";
import { Mod } from "../../../model/mod.model";
import { SaveEnabledModsRequest, ServerModsService } from "../../../service/server-mods.service";
import { NotificationService } from "../../../service/notification.service";
import { MaskService } from "../../../service/mask.service";
import { NotManagedModsComponent } from "../not-managed-mods/not-managed-mods.component";
import { Subject, Subscription } from "rxjs";
import { ModListComponent } from "../mod-list/mod-list.component";

@Component({
  selector: "app-mod-lists",
  imports: [CdkDropListGroup, NotManagedModsComponent, ModListComponent],
  templateUrl: "./mod-lists.component.html",
  styleUrl: "./mod-lists.component.scss"
})
export class ModListsComponent implements OnInit, OnDestroy {
  private readonly maskService: MaskService = inject(MaskService);
  private readonly notificationService: NotificationService = inject(NotificationService);
  private readonly modService: ServerModsService = inject(ServerModsService);

  notManagedMods: Mod[] = [];
  disabledMods: Mod[] = [];
  enabledMods: Mod[] = [];

  reloadModsDataSubject!: Subject<any>;
  reloadModsDataSubscription!: Subscription;

  @ViewChildren(ModListComponent) modListComponents!: QueryList<ModListComponent>

  constructor() {
    this.reloadModsDataSubject = new Subject();
    this.reloadModsDataSubscription = this.reloadModsDataSubject.subscribe(() => {
      this.reloadMods();
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
      this.notManagedMods = modsResponse.notManagedMods;
      this.disabledMods = modsResponse.disabledMods;
      this.enabledMods = modsResponse.enabledMods;
      this.modListComponents.forEach(component => component.reload());

      this.maskService.hide();
      if (this.notManagedMods.length > 0) {
        this.notificationService.infoNotification(
          "ASWG detected some new mods. Scroll to the bottom to see them."
        );
      }
    });
  }

  onModItemDragDrop(event: CdkDragDrop<Mod[]>) {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      let movedMod = event.previousContainer.data[event.previousIndex];
      // let currentIndex: number;
      if (event.previousContainer.id == "Enabled") {
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
        event.currentIndex
      );
      this.modListComponents.forEach(component => component.sortModList());
    }
  }

  onModDelete(mod: Mod) {
    this.notificationService.successNotification("Mod has been deleted!");
    this.reloadModsDataSubject.next(null);
  }

  enableAllMods() {
    this.maskService.show();
    this.modService
      .saveEnabledMods({ mods: this.enabledMods.concat(this.disabledMods) })
      .subscribe((response) => {
        this.maskService.hide();
        this.reloadMods();
        this.notificationService.successNotification("Mods list updated!", "Success");
      });
  }

  disableAllMods() {
    this.maskService.show();
    this.modService
      .saveEnabledMods({ mods: [] } as SaveEnabledModsRequest)
      .subscribe((response) => {
        this.maskService.hide();
        this.reloadMods();
        this.notificationService.successNotification("Mods list updated!", "Success");
      });
  }

  private removeModFromList(list: Mod[], mod: Mod) {
    list.forEach((value, index) => {
      if (value == mod) list.splice(index, 1);
    });
  }

  public filterMods(searchPhrase: string) {
    this.modListComponents.forEach(component => component.filterMods(searchPhrase));
  }
}
