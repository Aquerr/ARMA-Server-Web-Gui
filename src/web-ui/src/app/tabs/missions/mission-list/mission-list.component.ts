import {
  Component,
  input,
  Input,
  OnChanges,
  OnInit,
  output,
  signal,
  WritableSignal
} from "@angular/core";
import { CdkDrag, CdkDragDrop, CdkDropList } from "@angular/cdk/drag-drop";
import { MissionListItemComponent } from "../mission-list-item/mission-list-item.component";
import { Mission } from "../../../model/mission.model";

@Component({
  selector: "app-mission-list",
  templateUrl: "./mission-list.component.html",
  imports: [
    CdkDropList,
    MissionListItemComponent,
    CdkDrag

  ],
  styleUrls: ["./mission-list.component.scss"]
})
export class MissionListComponent implements OnInit, OnChanges {
  @Input() listHeader!: string;
  @Input() moveAllMissionsButtonName!: string;
  @Input() moveAllMissionsButtonIcon!: string;
  @Input() sortEnabled!: boolean;

  missions = input<Mission[]>([]);
  moveAll = output<unknown>();
  missionItemDragDrop = output<CdkDragDrop<Mission[], Mission[], Mission>>();
  missionDelete = output<Mission>();

  filteredMissions: WritableSignal<Mission[]> = signal([]);

  ngOnInit() {
    this.reload();
  }

  ngOnChanges() {
    this.reload();
  }

  public reload() {
    this.filteredMissions.set([...this.missions()].sort((a, b) => a.name.localeCompare(b.name)));
  }

  onMissionItemDragDrop(event: CdkDragDrop<Mission[], Mission[], Mission>) {
    this.missionItemDragDrop.emit(event);
  }

  onMissionDelete(mod: Mission) {
    this.missionDelete.emit(mod);
  }

  filterMissions(searchPhrase: string) {
    this.filteredMissions.set(this.missions().filter((mod) => mod.name.toLowerCase()
      .includes(searchPhrase.toLowerCase())));
  }
}
