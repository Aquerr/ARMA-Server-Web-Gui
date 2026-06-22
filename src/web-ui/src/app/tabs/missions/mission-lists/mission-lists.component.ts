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
import { Mission } from "@model/mission.model";
import { LoadingSpinnerMaskService } from "@service/loading-spinner-mask.service";
import { NotificationService } from "@service/notification.service";
import { ServerMissionsService } from "@service/server-missions.service";
import { Subject, Subscription } from "rxjs";
import { moveItemBetweenSignalLists } from "@app/util/signal/signal-utils";
import { MissionListItemComponent } from "../mission-list-item/mission-list-item.component";
import { AswgDragAndDropListComponent } from "@common-ui/aswg-drag-and-drop-list/aswg-drag-and-drop-list.component";

@Component({
  selector: "app-mission-lists",
  templateUrl: "./mission-lists.component.html",
  imports: [
    CdkDropListGroup,
    MissionListItemComponent,
    AswgDragAndDropListComponent
  ],
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrls: ["./mission-lists.component.scss"]
})
export class MissionListsComponent implements OnInit, OnDestroy {
  private readonly maskService = inject(LoadingSpinnerMaskService);
  private readonly notificationService = inject(NotificationService);
  private readonly missionsService = inject(ServerMissionsService);

  searchPhrase = input<string>("");

  disabledMissions = signal<Mission[]>([]);
  enabledMissions = signal<Mission[]>([]);

  protected missionFilterFunction(mission: Mission, searchPhrase: string): boolean {
    return mission.name.toLowerCase().includes(searchPhrase.toLowerCase());
  }

  reloadMissionsDataSubject!: Subject<void>;
  reloadMissionsDataSubscription!: Subscription;

  @ViewChildren(AswgDragAndDropListComponent) listComponents!: QueryList<AswgDragAndDropListComponent<Mission>>;

  constructor() {
    this.reloadMissionsDataSubject = new Subject();
    this.reloadMissionsDataSubscription = this.reloadMissionsDataSubject.subscribe(() => {
      this.reloadMissions();
    });

    effect(() => {
      const searchPhrase = this.searchPhrase();
      this.listComponents?.forEach((component) => component.filterItems(searchPhrase));
    });
  }

  ngOnInit() {
    this.reloadMissions();
  }

  ngOnDestroy() {
    this.reloadMissionsDataSubscription.unsubscribe();
  }

  public reloadMissions() {
    this.maskService.show();
    this.missionsService.getInstalledMissions().subscribe((response) => {
      this.enabledMissions.set(response.enabledMissions);
      this.disabledMissions.set(response.disabledMissions);
      this.listComponents.forEach((component) => component.reload());
      this.maskService.hide();
    });
  }

  protected enableAllMissions() {
    this.maskService.show();
    this.missionsService
      .saveEnabledMissions({ missionTemplates: this.enabledMissions().concat(this.disabledMissions()).map((mission) => mission.template) })
      .subscribe(() => {
        this.maskService.hide();
        this.reloadMissions();
        this.notificationService.successNotification("Missions list has been updated!");
      });
  }

  protected onMissionItemDragDrop(event: CdkDragDrop<Mission[], Mission[], Mission>) {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      const movedMission = event.previousContainer.data[event.previousIndex];
      if (event.previousContainer.id == "Enabled") {
        moveItemBetweenSignalLists(this.enabledMissions, this.disabledMissions, movedMission);
      } else {
        moveItemBetweenSignalLists(this.disabledMissions, this.enabledMissions, movedMission);
      }

      // Update view drag drop list
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex
      );
    }
  }

  protected onMissionDelete(mission: Mission) {
    this.missionsService.deleteMission(mission.template).subscribe(() => {
      this.maskService.hide();
      this.notificationService.successNotification("Mission has been deleted!");
      this.reloadMissionsDataSubject.next();
    });
  }

  protected disableAllMissions() {
    this.maskService.show();
    this.missionsService
      .saveEnabledMissions({ missionTemplates: [] })
      .subscribe(() => {
        this.maskService.hide();
        this.reloadMissions();
        this.notificationService.successNotification("Missions list updated!");
      });
  }
}
