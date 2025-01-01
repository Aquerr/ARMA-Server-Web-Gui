import {inject, Injectable} from "@angular/core";
import {AbstractControl, FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AswgUser} from "../../../../service/users.service";

@Injectable({
  providedIn: 'root',
})
export class EditUserFormService {
  private readonly formBuilder: FormBuilder = inject(FormBuilder);

  getForm(): FormGroup {
    return this.formBuilder.group({
      id: [null],
      username: ['', Validators.required],
      password: ['', Validators.required],
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

  getIdControl(form: FormGroup): AbstractControl {
    return form.get('id') as AbstractControl;
  }

  getUsernameControl(form: FormGroup): AbstractControl {
    return form.get('username') as AbstractControl;
  }

  getPasswordControl(form: FormGroup): AbstractControl {
    return form.get('password') as AbstractControl;
  }

  getLockedControl(form: FormGroup): AbstractControl {
    return form.get('locked') as AbstractControl;
  }

  getAuthoritiesControl(form: FormGroup): AbstractControl {
    return form.get('authorities') as AbstractControl;
  }
}
