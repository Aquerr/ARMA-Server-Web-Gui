import { Component, Input, OnInit } from "@angular/core";
import { COMMA, ENTER } from "@angular/cdk/keycodes";
import { MatChipEditedEvent, MatChipInputEvent } from "@angular/material/chips";
import { AbstractControl, FormGroup } from "@angular/forms";
import { MatAutocompleteSelectedEvent } from "@angular/material/autocomplete";

@Component({
  selector: "aswg-chip-form-input",
  templateUrl: "./aswg-chip-form-input.component.html",
  styleUrl: "./aswg-chip-form-input.component.scss",
  standalone: false
})
export class AswgChipFormInputComponent implements OnInit {
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

  @Input()
  autocompleteList: string[] = [];

  autocompleteFilteredList: string[] = [];

  ngOnInit(): void {
    this.filterAutocompleteList();
  }

  getEntries(): string[] {
    return this.control.value;
  }

  addEntry(event: MatChipInputEvent) {
    const value = (event.value || "").trim();

    if (value) {
      this.addValue(value);
    }
    event.chipInput.clear();
  }

  addValue(value: string) {
    this.control.updateValueAndValidity();
    this.control.value.push(value);
    this.filterAutocompleteList();
  }

  removeValue(value: string) {
    const index = this.control.value.indexOf(value);
    if (index >= 0) {
      let list = this.control.value as string[];
      list.splice(index, 1);
      this.control.setValue(list);
      this.control.updateValueAndValidity();
      this.filterAutocompleteList();
    }
    this.filterAutocompleteList();
  }

  editEntry(oldValue: string, event: MatChipEditedEvent) {
    const value = event.value.trim();

    if (!value) {
      this.removeValue(value);
      return;
    }

    const index = this.control.value.indexOf(oldValue);
    if (index >= 0) {
      let list = this.control.value as string[];
      list[index] = value;
      this.control.setValue(list);
      this.filterAutocompleteList();
    }
  }

  removeEntry(value: string) {
    this.removeValue(value);
  }

  selected(event: MatAutocompleteSelectedEvent) {
    this.addValue(event.option.viewValue);
    event.option.deselect();
  }

  private filterAutocompleteList() {
    this.autocompleteFilteredList = this.autocompleteList.filter((item) => {
      return !this.control.value.includes(item);
    });
  }
}
