import { Component, input } from "@angular/core";
import { CdkDrag, CdkDragDrop, CdkDropList, moveItemInArray } from "@angular/cdk/drag-drop";
import { FormControl, FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatIcon } from "@angular/material/icon";
import { MatError, MatFormField, MatInput, MatLabel } from "@angular/material/input";
import { MatButton } from "@angular/material/button";
import { MotdItem } from "../general-form.service";

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
    CdkDrag,
    ReactiveFormsModule,
    MatError
  ],
  styleUrls: ["./motd-list.component.scss"]
})
export class MotdListComponent {
  motdControl = input.required<FormControl<MotdItem[]>>();
  motdIntervalControl = input.required<FormControl<number>>();

  deleteMotdLine(motdLineIndex: number) {
    this.motdControl().patchValue(this.motdControl().value.filter((value, index) => {
      return index != motdLineIndex;
    }));
  }

  addNewMotdLine() {
    this.motdControl().patchValue([...this.motdControl().value, new MotdItem("")]);
  }

  onReorderList(event: CdkDragDrop<string[]>) {
    moveItemInArray(this.motdControl().value, event.previousIndex, event.currentIndex);
  }

  onMotdItemDoubleClick(motdItem: MotdItem) {
    motdItem.editing = !motdItem.editing;
  }

  onItemUpdate(motdItem: MotdItem) {
    motdItem.editing = false;
  }
}
