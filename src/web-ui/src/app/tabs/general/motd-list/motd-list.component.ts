import { Component } from "@angular/core";
import { CdkDrag, CdkDragDrop, CdkDropList, moveItemInArray } from "@angular/cdk/drag-drop";
import { FormsModule } from "@angular/forms";
import { MatIcon } from "@angular/material/icon";
import { MatFormField, MatInput, MatLabel } from "@angular/material/input";
import { MatButton } from "@angular/material/button";

@Component({
  selector: "app-motd-list",
  templateUrl: "./motd-list.component.html",
  imports: [
    CdkDropList,
    FormsModule,
    MatIcon,
    MatFormField,
    MatLabel,
    MatInput,
    MatButton,
    CdkDrag
  ],
  styleUrls: ["./motd-list.component.scss"]
})
export class MotdListComponent {
  motd: MotdItem[] = [];
  motdInterval: number = 5;

  deleteMotdLine(motdLineIndex: number) {
    this.motd = this.motd.filter((value, index) => {
      return index != motdLineIndex;
    });
  }

  addNewMotdLine() {
    this.motd.push(new MotdItem(""));
  }

  populateModtList(motdList: string[]) {
    this.motd = motdList.map((message) => new MotdItem(message));
  }

  onReorderList(event: CdkDragDrop<string[]>) {
    moveItemInArray(this.motd, event.previousIndex, event.currentIndex);
  }

  onMotdItemDoubleClick(motdItem: MotdItem) {
    motdItem.editing = !motdItem.editing;
  }

  getMotdMessages() {
    return this.motd.map((item) => item.message);
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
