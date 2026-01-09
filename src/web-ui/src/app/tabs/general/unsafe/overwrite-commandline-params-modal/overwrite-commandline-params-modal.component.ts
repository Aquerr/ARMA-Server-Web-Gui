import { Component, inject, Inject } from "@angular/core";
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatButton } from "@angular/material/button";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent, MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import { MatFormField, MatLabel } from "@angular/material/form-field";
import { MatInput } from "@angular/material/input";

@Component({
  selector: "app-overwrite-commandline-params-modal",
  imports: [
    FormsModule,
    MatButton,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    MatDialogTitle,
    MatFormField,
    MatInput,
    MatLabel,
    ReactiveFormsModule
  ],
  templateUrl: "./overwrite-commandline-params-modal.component.html",
  styleUrl: "./overwrite-commandline-params-modal.component.scss"
})
export class OverwriteCommandlineParamsModalComponent {
  private readonly formBuilder: FormBuilder = inject(FormBuilder);
  private readonly dialogRef: MatDialogRef<OverwriteCommandlineParamsModalComponent> = inject<MatDialogRef<OverwriteCommandlineParamsModalComponent>>(
    MatDialogRef<OverwriteCommandlineParamsModalComponent>
  );

  form: FormGroup;

  constructor(@Inject(MAT_DIALOG_DATA) public currentParams: string) {
    this.form = this.formBuilder.group({
      parameters: [currentParams]
    });
  }

  public confirm() {
    this.dialogRef.close(this.form.get("parameters")?.value);
  }
}
