import { CdkDragDrop, moveItemInArray, transferArrayItem } from "@angular/cdk/drag-drop";
import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from "@angular/core";

@Component({
    selector: 'aswg-drag-drop-list',
    templateUrl: './aswg-drag-drop-list.component.html',
    styleUrls: ['./aswg-drag-drop-list.component.css']
})
export class AswgDragDropListComponent implements OnInit, OnDestroy {

    @Input() items: string[] = [];
    @Input() header: string = '';

    @Input() canSortManually: boolean = false;
    @Input() autoSort: boolean = false;
    @Input() canDelete: boolean = false;
    @Input() canModify: boolean = false;

    @Output() deleteClicked: EventEmitter<string> = new EventEmitter<string>();
    @Output() modifyClicked: EventEmitter<string> = new EventEmitter<string>();

    constructor(){}

    ngOnDestroy(): void {

    }

    ngOnInit(): void {

    }

    drop(event: CdkDragDrop<string[]>){
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
          this.items.sort();
        }
    }

  deleteClick(item: string) {
      this.deleteClicked.emit(item);
  }

  modifyClick(item: string) {
      this.modifyClicked.emit(item);
  }
}
