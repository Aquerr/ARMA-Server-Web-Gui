import { CdkDragDrop, moveItemInArray, transferArrayItem } from "@angular/cdk/drag-drop";
import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from "@angular/core";
import {Mission} from "../../../model/mission.model";

@Component({
    selector: 'app-missions-list',
    templateUrl: './missions-list.component.html',
    styleUrls: ['./missions-list.component.css']
})
export class MissionsListComponent implements OnInit, OnDestroy {

  @Input() missions: Mission[] = [];

  @Input() header: string = '';

  @Input() canSortManually: boolean = false;
  @Input() autoSort: boolean = false;
  // @Input() canDelete: boolean = false;
  // @Input() canModify: boolean = false;

  @Output() deleteClicked: EventEmitter<string> = new EventEmitter<string>();
  @Output() modifyClicked: EventEmitter<Mission> = new EventEmitter<Mission>();

  constructor(){}

  ngOnDestroy(): void {

  }

  ngOnInit(): void {}

  drop(event: CdkDragDrop<Mission[]>){
      if (event.previousContainer === event.container){
          moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
      } else {
          transferArrayItem(
              event.previousContainer.data,
              event.container.data,
              event.previousIndex,
              event.currentIndex,
          );
      }
      if (this.autoSort)
      {
        this.missions.sort((a, b) => a.name.localeCompare(b.name));
      }
  }

  deleteClick(mission: Mission) {
      this.deleteClicked.emit(mission.name);
  }

  modifyClick(mission: Mission) {
      this.modifyClicked.emit(mission);
  }
}
