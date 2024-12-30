import {Component, Input} from '@angular/core';
import {COMMA, ENTER} from "@angular/cdk/keycodes";
import {
  MatChipEditedEvent,
  MatChipInputEvent,
} from "@angular/material/chips";
import {AbstractControl, FormGroup} from "@angular/forms";

@Component({
    selector: 'aswg-chip-form-input',
    templateUrl: './aswg-chip-form-input.component.html',
    styleUrl: './aswg-chip-form-input.component.scss',
    standalone: false
})
export class AswgChipFormInputComponent {

  protected readonly ENTER = ENTER;
  protected readonly COMMA = COMMA;

  @Input()
  control!: AbstractControl<any, any>;
  @Input()
  parent!: FormGroup;
  @Input()
  labelText!: string;

  @Input()
  toolTipText: string = "";

  getEntries(): string[] {
    return this.control.value;
  }

  addEntry(event: MatChipInputEvent) {
    const value = (event.value || '').trim();

    if (value) {
      this.control.value.push(value);
    }
    this.control.updateValueAndValidity();

    event.chipInput.clear();
  }

  editEntry(fileExtension: string, event: MatChipEditedEvent) {
    const value = event.value.trim();

    if (!value) {
      this.removeEntry(value);
      return;
    }

    const index = this.control.value.indexOf(fileExtension);
    if (index >= 0) {
      let list = this.control.value as string[];
      list[index] = value;
      this.control.setValue(list);
    }
  }

  removeEntry(fileExtension: string) {
    const index = this.control.value.indexOf(fileExtension);
    if (index >= 0) {
      let list = this.control.value as string[];
      list.splice(index, 1);
      this.control.setValue(list);
      this.control.updateValueAndValidity();
    }
  }
}
