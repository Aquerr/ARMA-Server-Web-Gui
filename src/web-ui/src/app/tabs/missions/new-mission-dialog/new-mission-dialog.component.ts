import { Component } from "@angular/core";
import {
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import { FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from "@angular/forms";
import { MatFormField, MatInput, MatLabel } from "@angular/material/input";
import { MatOption, MatSelect } from "@angular/material/select";
import { DragAndDropFileDirective } from "../../../common-ui/directive/drag-and-drop-file.directive";
import { MatIcon } from "@angular/material/icon";
import { MatButton } from "@angular/material/button";

@Component({
  selector: "app-new-mission-dialog",
  templateUrl: "./new-mission-dialog.component.html",
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatFormField,
    MatLabel,
    MatSelect,
    MatOption,
    FormsModule,
    ReactiveFormsModule,
    DragAndDropFileDirective,
    MatIcon,
    MatDialogActions,
    MatButton,
    MatDialogClose,
    MatInput
  ],
  styleUrl: "./new-mission-dialog.component.scss"
})
export class NewMissionDialogComponent {
  missionType: "BUILT_IN" | "FILE" = "BUILT_IN";
  file: File | null = null;
  form: FormGroup<{ name: FormControl<string>; template: FormControl<string> }>;

  constructor(
    private dialogRef: MatDialogRef<NewMissionDialogComponent>,
    formBuilder: FormBuilder
  ) {
    this.form = formBuilder.nonNullable.group({
      name: formBuilder.nonNullable.control(""),
      template: formBuilder.nonNullable.control("", [Validators.required, Validators.pattern("^\\S*$")])
    });
  }

  add() {
    this.closeDialog();
  }

  onFileDropped(file: File) {
    this.file = file;
  }

  onFileSelected(event: Event) {
    const target = event.target as HTMLInputElement;

    if (!target.files) return;

    const file: File = target.files[0];
    if (file) {
      this.file = file;
    }
  }

  closeDialog() {
    if (this.missionType == "BUILT_IN") {
      this.form.markAllAsTouched();
      if (!this.form.valid) return;
    }

    this.dialogRef.close({
      name: this.form.get("name")?.value,
      template: this.form.get("template")?.value,
      file: this.file
    });
  }
}
