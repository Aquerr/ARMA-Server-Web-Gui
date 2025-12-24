import { ChangeDetectionStrategy, Component, Inject, inject, OnInit } from "@angular/core";
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators
} from "@angular/forms";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { AswgUser } from "../../service/users.service";

export const passwordsMatchValidator: ValidatorFn = (
  control: AbstractControl
): ValidationErrors | null => {
  return control.get("password")?.value === control.get("confirmPassword")?.value
    ? null
    : { passwordsDoNotMatch: true };
};

@Component({
  selector: "app-password-change-dialog",
  templateUrl: "./password-change-dialog.component.html",
  standalone: false,
  styleUrl: "./password-change-dialog.component.scss",
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PasswordChangeDialogComponent {
  private readonly formBuilder: FormBuilder = inject(FormBuilder);
  private readonly dialogRef: MatDialogRef<PasswordChangeDialogComponent> = inject(
    MatDialogRef<PasswordChangeDialogComponent>
  );

  form: FormGroup;

  constructor(@Inject(MAT_DIALOG_DATA) public user: AswgUser) {
    this.form = this.formBuilder.group(
      {
        password: ["", Validators.required],
        confirmPassword: [""]
      },
      { validators: [passwordsMatchValidator] }
    );
  }

  confirm() {
    this.form.markAllAsTouched();
    if (this.form.valid) {
      this.dialogRef.close(this.form.get("password")?.value);
    }
  }
}
