import { inject, Injectable } from "@angular/core";
import { AbstractControl, FormBuilder, FormControl, FormGroup, Validators } from "@angular/forms";
import { AswgUser } from "../../../../service/users.service";

export interface UserEditFormControls {
  id: FormControl<number | null>;
  username: FormControl<string>;
  password: FormControl<string | null>;
  locked: FormControl<boolean>;
  authorities: FormControl<string[]>;
}

@Injectable({
  providedIn: "root"
})
export class EditUserFormService {
  private readonly formBuilder: FormBuilder = inject(FormBuilder);

  getForm(): FormGroup<UserEditFormControls> {
    return this.formBuilder.group({
      id: this.formBuilder.control<number | null>(null),
      username: this.formBuilder.nonNullable.control("", [Validators.required]),
      password: this.formBuilder.control<string | null>(""),
      locked: this.formBuilder.nonNullable.control(false, [Validators.required]),
      authorities: this.formBuilder.nonNullable.control<string[]>([])
    });
  }

  setForm(form: FormGroup<UserEditFormControls>, aswgUser: AswgUser) {
    form.setValue({
      id: aswgUser.id,
      username: aswgUser.username,
      password: aswgUser.password,
      locked: aswgUser.locked,
      authorities: aswgUser.authorities
    });
  }

  asAswgUser(form: FormGroup<UserEditFormControls>): AswgUser {
    const formValue = form.value;
    return {
      id: formValue.id,
      username: formValue.username,
      password: formValue.password,
      locked: formValue.locked,
      authorities: formValue.authorities
    } as AswgUser;
  }
}
