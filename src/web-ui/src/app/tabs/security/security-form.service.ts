import {Injectable} from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {GetServerSecurityResponse, SaveServerSecurityRequest} from '../../service/server-security.service';

@Injectable({
  providedIn: 'root'
})
export class SecurityFormService {
  constructor(private fb: FormBuilder) {
  }

  getForm(): FormGroup {
    return this.fb.group({
      serverPassword: [''],
      serverAdminPassword: [''],
      serverCommandPassword: [''],
      battleEye: [true, [Validators.required]],
      verifySignatures: [true, [Validators.required]],
      allowedFilePatching: [0, [Validators.required]],
      allowedLoadFileExtensions: [[]]
    });
  }

  setForm(form: FormGroup, data: GetServerSecurityResponse) {
    this.getServerPasswordControl(form).setValue(data.serverPassword);
    this.getServerAdminPasswordControl(form).setValue(data.serverAdminPassword);
    this.getServerCommandPasswordControl(form).setValue(data.serverCommandPassword);
    this.getBattleEyeControl(form).setValue(data.battleEye);
    this.getVerifySignaturesControl(form).setValue(data.verifySignatures);
    this.getAllowedFilePatchingControl(form).setValue(data.allowedFilePatching);
    this.getAllowedLoadFileExtensions(form).setValue(data.allowedLoadFileExtensions);
  }

  get(form: FormGroup) {
    return {
      serverPassword: this.getServerPasswordControl(form).value,
      serverAdminPassword: this.getServerAdminPasswordControl(form).value,
      serverCommandPassword: this.getServerCommandPasswordControl(form).value,
      battleEye: this.getBattleEyeControl(form).value,
      verifySignatures: this.getVerifySignaturesControl(form).value,
      allowedFilePatching: this.getAllowedFilePatchingControl(form).value,
      allowedLoadFileExtensions: this.getAllowedLoadFileExtensions(form).value
    } as SaveServerSecurityRequest;
  }

  getServerPasswordControl(form: FormGroup) {
    return form.get('serverPassword') as AbstractControl;
  }

  getServerAdminPasswordControl(form: FormGroup) {
    return form.get('serverAdminPassword') as AbstractControl;
  }

  getServerCommandPasswordControl(form: FormGroup) {
    return form.get('serverCommandPassword') as AbstractControl;
  }

  getBattleEyeControl(form: FormGroup) {
    return form.get('battleEye') as AbstractControl;
  }

  getVerifySignaturesControl(form: FormGroup) {
    return form.get('verifySignatures') as AbstractControl;
  }

  getAllowedFilePatchingControl(form: FormGroup) {
    return form.get('allowedFilePatching') as AbstractControl;
  }

  getAllowedLoadFileExtensions(form: FormGroup) {
    return form.get('allowedLoadFileExtensions') as AbstractControl;
  }
}
