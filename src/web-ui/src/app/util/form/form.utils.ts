import { AbstractControl } from "@angular/forms";

export function stripToDecimals(control: AbstractControl<string>): void {
  const value = control.value;
  control.setValue(value.replace(/[^.\d]/, ""));
}

export function stripToDigits(control: AbstractControl<string | number>): void {
  const value = control.value;
  control.setValue(digitsOnly(value.toString()));
}

export function digitsOnly(value: string): string {
  return value?.replace(/\D+/, "");
}
