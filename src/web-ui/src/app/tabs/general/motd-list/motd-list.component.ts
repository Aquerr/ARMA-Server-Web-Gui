import {Component, OnInit} from '@angular/core';
import {CdkDragDrop, moveItemInArray} from "@angular/cdk/drag-drop";

@Component({
    selector: 'app-motd-list',
    templateUrl: './motd-list.component.html',
    styleUrls: ['./motd-list.component.scss'],
    standalone: false
})
export class MotdListComponent implements OnInit {

  motd: MotdItem[] = [];
  motdInterval: number = 5;

  constructor() { }

  ngOnInit(): void {
  }

  deleteMotdLine(motdLineIndex: number) {
    this.motd = this.motd.filter((value, index) => {
      return index != motdLineIndex;
    });
  }

  addNewMotdLine() {
    this.motd.push(new MotdItem(""));
  }

  pupulateModtList(motdList: string[]) {
    this.motd = motdList.map(message => new MotdItem(message));
  }

  onReorderList(event: CdkDragDrop<string[]>) {
    moveItemInArray(this.motd, event.previousIndex, event.currentIndex);
  }

  onMotdItemDoubleClick(motdItem: MotdItem) {
    motdItem.editing = !motdItem.editing;
  }

  getMotdMessages() {
    return this.motd.map(item => item.message);
  }

  onItemUpdate(motdItem: MotdItem) {
    motdItem.editing = false;
  }
}

class MotdItem {
  message: string = "";
  editing: boolean = false;

  constructor(message: string) {
    this.message = message;
  }
}
