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
import { AswgUser, UsersService } from "../../../../../service/users.service";
import { LoadingSpinnerMaskService } from "../../../../../service/loading-spinner-mask.service";
import { NotificationService } from "../../../../../service/notification.service";

export const passwordsMatchValidator: ValidatorFn = (
  control: AbstractControl
): ValidationErrors | null => {
  return control.get("password")?.value === control.get("confirmPassword")?.value
    ? null
    : { passwordsDoNotMatch: true };
};

@Component({
  selector: "app-password-change-modal",
  templateUrl: "./password-change-modal.component.html",
  standalone: false,
  styleUrl: "./password-change-modal.component.scss",
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PasswordChangeModalComponent {
  private readonly formBuilder: FormBuilder = inject(FormBuilder);
  private readonly dialogRef: MatDialogRef<PasswordChangeModalComponent> = inject(
    MatDialogRef<PasswordChangeModalComponent>
  );
  private readonly usersService: UsersService = inject(UsersService);
  private readonly maskService: LoadingSpinnerMaskService = inject(LoadingSpinnerMaskService);
  private readonly notificationService: NotificationService = inject(NotificationService);

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
      this.maskService.show();
      this.usersService
        .updatePassword(this.user.id!, this.form.get("password")?.value)
        .subscribe((response) => {
          this.notificationService.successNotification("Password has been updated!");
          this.maskService.hide();
          this.dialogRef.close();
        });
    }
  }
}
