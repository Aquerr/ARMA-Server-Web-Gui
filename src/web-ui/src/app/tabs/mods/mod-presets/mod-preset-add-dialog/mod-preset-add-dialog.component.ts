import { Component } from "@angular/core";
import { MatDialogRef } from "@angular/material/dialog";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";

@Component({
  selector: "app-mod-preset-add-dialog",
  templateUrl: "./mod-preset-add-dialog.component.html",
  styleUrls: ["./mod-preset-add-dialog.component.scss"],
  standalone: false
})
export class ModPresetAddDialogComponent {
  form: FormGroup;

  constructor(
    private dialogRef: MatDialogRef<ModPresetAddDialogComponent>,
    formBuilder: FormBuilder
  ) {
    this.form = formBuilder.group({
      modPresetName: ["", [Validators.required]]
    });
  }

  onEnterClick($event: KeyboardEvent) {
    if ($event.code === "Enter") {
      this.closeDialog();
    }
  }

  prepareDialogResult() {
    return { create: true, modPresetName: this.form.get("modPresetName")?.value };
  }

  closeDialog() {
    this.form.markAllAsTouched();
    if (this.form.valid) {
      this.dialogRef.close(this.prepareDialogResult());
    }
  }

  hasFormError(formControlName: string, errorType: string) {
    return this.form.get(formControlName)?.hasError(errorType);
  }
}
