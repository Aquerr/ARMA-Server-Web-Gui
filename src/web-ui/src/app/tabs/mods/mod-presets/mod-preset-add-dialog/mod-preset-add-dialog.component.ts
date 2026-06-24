import { Component, ChangeDetectionStrategy } from "@angular/core";
import {
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { MatError, MatFormField, MatInput, MatLabel } from "@angular/material/input";
import { MatButton } from "@angular/material/button";

export interface ModPresetFormControls {
  modPresetName: FormControl<string>;
}

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
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ["./mod-preset-add-dialog.component.scss"]
})
export class ModPresetAddDialogComponent {
  form: FormGroup<ModPresetFormControls>;

  constructor(
    private dialogRef: MatDialogRef<ModPresetAddDialogComponent>,
    formBuilder: FormBuilder
  ) {
    this.form = formBuilder.group<ModPresetFormControls>({
      modPresetName: formBuilder.nonNullable.control("", [Validators.required])
    });
  }

  onEnterClick($event: KeyboardEvent) {
    if ($event.code === "Enter") {
      this.closeDialog();
    }
  }

  prepareDialogResult() {
    return { create: true, modPresetName: this.form.controls.modPresetName.value };
  }

  closeDialog() {
    this.form.markAllAsTouched();
    if (this.form.valid) {
      this.dialogRef.close(this.prepareDialogResult());
    }
  }
}
