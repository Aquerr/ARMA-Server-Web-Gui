import { ChangeDetectionStrategy, Component, Inject, inject } from "@angular/core";
import {
  AbstractControl,
  FormBuilder, FormControl,
  FormGroup, ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators
} from "@angular/forms";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose, MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import { AswgUser } from "../../service/users.service";
import { MatError, MatFormField, MatInput, MatLabel } from "@angular/material/input";
import { MatButton } from "@angular/material/button";

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
  styleUrl: "./password-change-dialog.component.scss",
  imports: [
    MatFormField,
    ReactiveFormsModule,
    MatLabel,
    MatError,
    MatDialogActions,
    MatButton,
    MatDialogClose,
    MatInput,
    MatDialogTitle,
    MatDialogContent
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PasswordChangeDialogComponent {
  private readonly formBuilder: FormBuilder = inject(FormBuilder);
  private readonly dialogRef = inject<MatDialogRef<PasswordChangeDialogComponent, string | null>>(
    MatDialogRef<PasswordChangeDialogComponent, string | null>
  );

  form: FormGroup<{ password: FormControl<string>; confirmPassword: FormControl<string> }>;

  constructor(@Inject(MAT_DIALOG_DATA) public user: AswgUser) {
    this.form = this.formBuilder.nonNullable.group(
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
