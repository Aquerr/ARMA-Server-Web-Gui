import {Component, Input} from '@angular/core';
import {COMMA, ENTER} from "@angular/cdk/keycodes";
import {
  MatChipEditedEvent,
  MatChipInputEvent
} from "@angular/material/chips";

@Component({
  selector: 'aswg-chip-input',
  templateUrl: './aswg-chip-input.component.html',
  styleUrl: './aswg-chip-input.component.scss'
})
export class AswgChipInputComponent {

  protected readonly ENTER = ENTER;
  protected readonly COMMA = COMMA;

  @Input()
  labelText: string = "";
  @Input()
  toolTipText: string = "";
  @Input("data")
  list: string[] = [];

  editEntry(localClientIp: string, event: MatChipEditedEvent) {
    const value = event.value.trim();

    if (!value) {
      this.removeEntry(value);
      return;
    }

    const index = this.list.indexOf(localClientIp);
    if (index >= 0) {
      this.list[index] = value;
    }
  }

  removeEntry(localClientIp: string) {
    const index = this.list.indexOf(localClientIp);
    if (index >= 0) {
      this.list.splice(index, 1);
    }
  }

  addEntry(event: MatChipInputEvent) {
    const value = (event.value || '').trim();

    if (value) {
      this.list.push(value);
    }

    event.chipInput.clear();
  }
}
