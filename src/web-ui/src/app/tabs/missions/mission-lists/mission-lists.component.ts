import { Component, inject, input, QueryList, signal, ViewChildren, OnInit, OnDestroy, effect } from "@angular/core";
import { CdkDragDrop, CdkDropListGroup, moveItemInArray, transferArrayItem } from "@angular/cdk/drag-drop";
import { MissionListComponent } from "../mission-list/mission-list.component";
import { Mission } from "../../../model/mission.model";
import { LoadingSpinnerMaskService } from "../../../service/loading-spinner-mask.service";
import { NotificationService } from "../../../service/notification.service";
import { SaveEnabledMissionsRequest, ServerMissionsService } from "../../../service/server-missions.service";
import { Subject, Subscription } from "rxjs";
import { moveItemBetweenSignalLists } from "../../../util/signal/signal-utils";

@Component({
  selector: "app-mission-lists",
  templateUrl: "./mission-lists.component.html",
  imports: [
    CdkDropListGroup,
    MissionListComponent
  ],
  styleUrls: ["./mission-lists.component.scss"]
})
export class MissionListsComponent implements OnInit, OnDestroy {
  private readonly maskService = inject(LoadingSpinnerMaskService);
  private readonly notificationService = inject(NotificationService);
  private readonly missionsService = inject(ServerMissionsService);

  searchPhrase = input<string>("");

  disabledMissions = signal<Mission[]>([]);
  enabledMissions = signal<Mission[]>([]);

  reloadMissionsDataSubject!: Subject<void>;
  reloadMissionsDataSubscription!: Subscription;

  @ViewChildren(MissionListComponent) missionListComponents!: QueryList<MissionListComponent>;

  constructor() {
    this.reloadMissionsDataSubject = new Subject();
    this.reloadMissionsDataSubscription = this.reloadMissionsDataSubject.subscribe(() => {
      this.reloadMissions();
    });

    effect(() => {
      const searchPhrase = this.searchPhrase();
      this.missionListComponents?.forEach((component) => component.filterMissions(searchPhrase));
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
      this.missionListComponents.forEach((component) => component.reload());
      this.maskService.hide();
    });
  }

  protected enableAllMissions() {
    this.maskService.show();
    this.missionsService
      .saveEnabledMissions({ missions: this.enabledMissions().concat(this.disabledMissions()) })
      .subscribe(() => {
        this.maskService.hide();
        this.reloadMissions();
        this.notificationService.successNotification("Missions list has been updated!", "Success");
      });
  }

  protected onMissionItemDragDrop(event: CdkDragDrop<Mission[], Mission[], Mission>) {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      const movedMod = event.previousContainer.data[event.previousIndex];
      if (event.previousContainer.id == "Enabled") {
        moveItemBetweenSignalLists(this.enabledMissions, this.disabledMissions, movedMod);
      } else {
        moveItemBetweenSignalLists(this.disabledMissions, this.enabledMissions, movedMod);
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

  protected onMissionDelete() {
    this.notificationService.successNotification("Mission has been deleted!");
    this.reloadMissionsDataSubject.next();
  }

  protected disableAllMissions() {
    this.maskService.show();
    this.missionsService
      .saveEnabledMissions({ missions: [] } as SaveEnabledMissionsRequest)
      .subscribe(() => {
        this.maskService.hide();
        this.reloadMissions();
        this.notificationService.successNotification("Missions list updated!", "Success");
      });
  }
}
