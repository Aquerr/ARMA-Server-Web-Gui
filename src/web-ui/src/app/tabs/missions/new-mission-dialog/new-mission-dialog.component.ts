import { Component } from '@angular/core';
import {MatDialogRef} from "@angular/material/dialog";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
    selector: 'app-new-mission-dialog',
    templateUrl: './new-mission-dialog.component.html',
    styleUrl: './new-mission-dialog.component.scss',
    standalone: false
})
export class NewMissionDialogComponent {
  missionType: 'BUILT_IN' | 'FILE' = 'BUILT_IN';
  file: File | null = null;
  form: FormGroup;

  constructor(private dialogRef: MatDialogRef<NewMissionDialogComponent>,
              formBuilder: FormBuilder) {
    this.form = formBuilder.group({
      name: new FormControl(''),
      template: new FormControl(
        '', [Validators.required, Validators.pattern("^\\S*$")]
      )
    });
  }

  add() {
    this.closeDialog();
  }

  onFileDropped(file: File) {
    this.file = file;
  }

  onFileSelected(event: Event) {
    const target = (event.target as HTMLInputElement);

    if (!target.files)
      return;

    const file : File = target.files[0];
    if (file) {
      this.file = file;
    }
  }

  closeDialog() {
    if (this.missionType == "BUILT_IN") {
      this.form.markAllAsTouched();
      if (!this.form.valid)
        return;
    }

    this.dialogRef.close(
      {
        name: this.form.get('name')?.value,
        template: this.form.get('template')?.value,
        file: this.file
      }
    );
  }
}
