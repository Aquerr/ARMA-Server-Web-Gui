import { Component } from "@angular/core";
import {
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { MatError, MatFormField, MatInput, MatLabel } from "@angular/material/input";
import { MatButton } from "@angular/material/button";

@Component({
  selector: "app-mod-preset-add-dialog",
  templateUrl: "./mod-preset-add-dialog.component.html",
  imports: [
    MatDialogTitle,
    MatDialogContent,
    ReactiveFormsModule,
    MatFormField,
    MatLabel,
    MatInput,
    MatError,
    MatDialogActions,
    MatButton,
    MatDialogClose
  ],
  styleUrls: ["./mod-preset-add-dialog.component.scss"]
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
    return { create: true, modPresetName: this.form.get("modPresetName")?.value as string };
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
