import { CdkDragDrop, moveItemInArray, transferArrayItem } from "@angular/cdk/drag-drop";
import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from "@angular/core";
import {Mod} from "../../../model/mod.model";

@Component({
    selector: 'app-mods-list',
    templateUrl: './mods-list.component.html',
    styleUrls: ['./mods-list.component.css']
})
export class ModsListComponent implements OnInit, OnDestroy {

    @Input() mods: Mod[] = [];
    @Input() header: string = '';

    @Input() canSortManually: boolean = false;
    @Input() autoSort: boolean = false;

    @Output() deleteClicked: EventEmitter<string> = new EventEmitter<string>();

    constructor(){}

    ngOnDestroy(): void {

    }

    ngOnInit(): void {

    }

    drop(event: CdkDragDrop<Mod[]>){
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
          this.mods.sort((a, b) => a.name.localeCompare(b.name));
        }
    }

  deleteClick(modName: string) {
      this.deleteClicked.emit(modName);
  }
}
