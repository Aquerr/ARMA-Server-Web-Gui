import { inject, Injectable } from "@angular/core";
import { AbstractControl, FormBuilder, FormGroup, Validators } from "@angular/forms";
import { AswgUser } from "../../../../service/users.service";

@Injectable({
  providedIn: "root"
})
export class EditUserFormService {
  private readonly formBuilder: FormBuilder = inject(FormBuilder);

  getForm(): FormGroup {
    return this.formBuilder.group({
      id: [null],
      username: ["", Validators.required],
      password: [""],
      locked: [false, Validators.required],
      authorities: [[]]
    });
  }

  setForm(form: FormGroup, aswgUser: AswgUser) {
    this.getIdControl(form).setValue(aswgUser.id);
    this.getUsernameControl(form).setValue(aswgUser.username);
    this.getPasswordControl(form).setValue(aswgUser.password);
    this.getLockedControl(form).setValue(aswgUser.locked);
    this.getAuthoritiesControl(form).setValue(aswgUser.authorities);
  }

  asAswgUser(form: FormGroup): AswgUser {
    return {
      id: this.getIdControl(form).value,
      username: this.getUsernameControl(form).value,
      password: this.getPasswordControl(form).value,
      locked: this.getLockedControl(form).value,
      authorities: this.getAuthoritiesControl(form).value
    } as AswgUser;
  }

  getIdControl(form: FormGroup): AbstractControl<number | null> {
    return form.get("id") as AbstractControl<number | null>;
  }

  getUsernameControl(form: FormGroup): AbstractControl<string> {
    return form.get("username") as AbstractControl<string>;
  }

  getPasswordControl(form: FormGroup): AbstractControl<string> {
    return form.get("password") as AbstractControl<string>;
  }

  getLockedControl(form: FormGroup): AbstractControl<boolean> {
    return form.get("locked") as AbstractControl<boolean>;
  }

  getAuthoritiesControl(form: FormGroup): AbstractControl<string[]> {
    return form.get("authorities") as AbstractControl<string[]>;
  }
}
