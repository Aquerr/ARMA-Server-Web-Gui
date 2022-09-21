import {Component, OnInit} from '@angular/core';
import {CdkDragDrop, moveItemInArray} from "@angular/cdk/drag-drop";

@Component({
  selector: 'app-motd-list',
  templateUrl: './motd-list.component.html',
  styleUrls: ['./motd-list.component.css']
})
export class MotdListComponent implements OnInit {

  motd: string[] = [];
  motdLine: string = "";

  constructor() { }

  ngOnInit(): void {
  }

  deleteMotdLite(motdLineIndex: number) {
    this.motd = this.motd.filter((value, index) => {
      return index != motdLineIndex;
    });
  }

  addNewMotdLine() {
    this.motd.push(this.motdLine);
    this.motdLine = "";
  }

  onReorderList(event: CdkDragDrop<string[]>) {
    moveItemInArray(this.motd, event.previousIndex, event.currentIndex);
  }
}
