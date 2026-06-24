import { Component, ChangeDetectionStrategy, InputSignal, input, model, computed } from "@angular/core";
import { COMMA, ENTER } from "@angular/cdk/keycodes";
import {
  MatChipEditedEvent,
  MatChipGrid,
  MatChipInput,
  MatChipInputEvent, MatChipRemove,
  MatChipRow
} from "@angular/material/chips";
import { AbstractControl, FormsModule } from "@angular/forms";
import {
  MatAutocomplete,
  MatAutocompleteSelectedEvent,
  MatAutocompleteTrigger,
  MatOption
} from "@angular/material/autocomplete";
import { MatFormField, MatLabel } from "@angular/material/form-field";
import { MatIcon } from "@angular/material/icon";
import { MatTooltip } from "@angular/material/tooltip";
import { toObservable, toSignal } from "@angular/core/rxjs-interop";
import { startWith, switchMap } from "rxjs";

@Component({
  selector: "app-aswg-chip-form-input",
  templateUrl: "./aswg-chip-form-input.component.html",
  styleUrl: "./aswg-chip-form-input.component.scss",
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    MatAutocompleteTrigger,
    MatAutocomplete,
    MatFormField,
    MatChipGrid,
    MatChipRow,
    MatIcon,
    MatChipInput,
    MatOption,
    MatLabel,
    MatTooltip,
    MatChipRemove,
    FormsModule
  ]
})
export class AswgChipFormInputComponent {
  protected readonly ENTER = ENTER;
  protected readonly COMMA = COMMA;

  public readonly control: InputSignal<AbstractControl<string[]>> = input.required();
  public readonly labelText: InputSignal<string> = input.required();
  public readonly toolTipText: InputSignal<string> = input("");
  public readonly sort: InputSignal<(a: string, b: string) => number> = input((a, b) => a?.localeCompare(b));
  public readonly availableOptions: InputSignal<string[]> = input<string[]>([]);

  protected readonly entries = toSignal(
    toObservable(this.control).pipe(
      switchMap((control) => control.valueChanges.pipe(
        startWith(control.value)
      ))
    ),
    { initialValue: [] as string[] }
  );

  protected currentValue = model("");
  protected availableOptionsFiltered = computed(() => {
    const unusedOptions = this.availableOptions().filter((item) => {
      return !this.control().value.includes(item);
    });

    const value = this.currentValue().toLowerCase();
    return this.currentValue()
      ? unusedOptions.filter((option) => option.toLowerCase().includes(value))
      : unusedOptions.slice();
  });

  getEntries(): string[] {
    return this.entries()?.sort(this.sort());
  }

  addEntry(event: MatChipInputEvent) {
    const value = (event.value || "").trim();

    if (value) {
      if (!this.hasOptions()) {
        this.addValue(value);
      } else {
        const option = this.availableOptions().find((option) => option.toLowerCase().includes(value.toLowerCase()));
        if (option) {
          this.addValue(option);
        }
      }
    }
    event.chipInput.clear();
  }

  addValue(value: string) {
    this.control().value.push(value);
    this.control().updateValueAndValidity();
  }

  removeValue(value: string) {
    const index = this.control().value.indexOf(value);
    if (index >= 0) {
      const list = this.control().value;
      list.splice(index, 1);
      this.control().setValue(list);
      this.control().updateValueAndValidity();
    }
  }

  editEntry(oldValue: string, event: MatChipEditedEvent) {
    const value = event.value.trim();

    if (!value) {
      this.removeValue(value);
      return;
    }

    if (!this.hasOptions() || this.availableOptions().some((option) => option.toLowerCase().includes(value.toLowerCase()))) {
      const index = this.control().value.indexOf(oldValue);
      if (index >= 0) {
        const list = this.control().value;
        list[index] = value;
        this.control().setValue(list);
      }
    }
  }

  removeEntry(value: string) {
    this.removeValue(value);
  }

  selected(event: MatAutocompleteSelectedEvent) {
    this.addValue(event.option.viewValue);
    event.option.deselect();
  }

  private hasOptions(): boolean {
    return this.availableOptions().length > 0;
  }
}
