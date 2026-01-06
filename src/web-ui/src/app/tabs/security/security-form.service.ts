import { Injectable } from "@angular/core";
import { AbstractControl, FormBuilder, FormGroup, Validators } from "@angular/forms";
import {
  AllowedFilePatching,
  GetServerSecurityResponse,
  SaveServerSecurityRequest
} from "../../service/server-security.service";
import { VoteCmd } from "../../model/vote-cmd.model";

@Injectable({
  providedIn: "root"
})
export class SecurityFormService {
  constructor(private readonly fb: FormBuilder) {}

  getForm(): FormGroup {
    return this.fb.group({
      serverPassword: [""],
      serverAdminPassword: [""],
      serverCommandPassword: [""],
      battleEye: [true, [Validators.required]],
      verifySignatures: [true, [Validators.required]],
      allowedFilePatching: [AllowedFilePatching.NOT_ALLOWED, [Validators.required]],
      filePatchingIgnoredClients: [[]],
      allowedLoadFileExtensions: [[]],
      adminUUIDs: [[]],
      allowedVoteCmds: [[]],
      kickDuplicate: [false, [Validators.required]],
      voteThreshold: ["0.5", [Validators.required]],
      voteMissionPlayers: [1, [Validators.required]]
    });
  }

  setForm(form: FormGroup, data: GetServerSecurityResponse) {
    this.getServerPasswordControl(form).setValue(data.serverPassword);
    this.getServerAdminPasswordControl(form).setValue(data.serverAdminPassword);
    this.getServerCommandPasswordControl(form).setValue(data.serverCommandPassword);
    this.getBattleEyeControl(form).setValue(data.battleEye);
    this.getVerifySignaturesControl(form).setValue(data.verifySignatures);
    this.getAllowedFilePatchingControl(form).setValue(data.allowedFilePatching);
    this.getFilePatchingIgnoredClientsControl(form).setValue(data.filePatchingIgnoredClients);
    this.getAllowedLoadFileExtensionsControl(form).setValue(data.allowedLoadFileExtensions);
    this.getAdminUUIDsControl(form).setValue(data.adminUUIDs);
    this.getAllowedVoteCmdsControl(form).setValue(data.allowedVoteCmds);
    this.getKickDuplicateControl(form).setValue(data.kickDuplicate);
    this.getVoteThresholdControl(form).setValue(data.voteThreshold);
    this.getVoteMissionPlayersControl(form).setValue(data.voteMissionPlayers);
  }

  get(form: FormGroup) {
    return {
      serverPassword: this.getServerPasswordControl(form).value,
      serverAdminPassword: this.getServerAdminPasswordControl(form).value,
      serverCommandPassword: this.getServerCommandPasswordControl(form).value,
      battleEye: this.getBattleEyeControl(form).value,
      verifySignatures: this.getVerifySignaturesControl(form).value,
      allowedFilePatching: this.getAllowedFilePatchingControl(form).value,
      filePatchingIgnoredClients: this.getFilePatchingIgnoredClientsControl(form).value,
      allowedLoadFileExtensions: this.getAllowedLoadFileExtensionsControl(form).value,
      adminUUIDs: this.getAdminUUIDsControl(form).value,
      allowedVoteCmds: this.getAllowedVoteCmdsControl(form).value,
      kickDuplicate: this.getKickDuplicateControl(form).value,
      voteThreshold: this.getVoteThresholdControl(form).value,
      voteMissionPlayers: this.getVoteMissionPlayersControl(form).value
    } as SaveServerSecurityRequest;
  }

  getServerPasswordControl(form: FormGroup) {
    return this.getControl(form, "serverPassword") as AbstractControl<string>;
  }

  getServerAdminPasswordControl(form: FormGroup) {
    return this.getControl(form, "serverAdminPassword") as AbstractControl<string>;
  }

  getServerCommandPasswordControl(form: FormGroup) {
    return this.getControl(form, "serverCommandPassword") as AbstractControl<string>;
  }

  getBattleEyeControl(form: FormGroup) {
    return this.getControl(form, "battleEye") as AbstractControl<boolean>;
  }

  getVerifySignaturesControl(form: FormGroup) {
    return this.getControl(form, "verifySignatures") as AbstractControl<boolean>;
  }

  getAllowedFilePatchingControl(form: FormGroup) {
    return this.getControl(form, "allowedFilePatching") as AbstractControl<number>;
  }

  getFilePatchingIgnoredClientsControl(form: FormGroup) {
    return this.getControl(form, "filePatchingIgnoredClients") as AbstractControl<string[]>;
  }

  getAllowedLoadFileExtensionsControl(form: FormGroup) {
    return this.getControl(form, "allowedLoadFileExtensions") as AbstractControl<string[]>;
  }

  getAdminUUIDsControl(form: FormGroup) {
    return this.getControl(form, "adminUUIDs") as AbstractControl<string[]>;
  }

  getAllowedVoteCmdsControl(form: FormGroup) {
    return this.getControl(form, "allowedVoteCmds") as AbstractControl<VoteCmd[]>;
  }

  getKickDuplicateControl(form: FormGroup) {
    return this.getControl(form, "kickDuplicate") as AbstractControl<boolean>;
  }

  getVoteThresholdControl(form: FormGroup) {
    return this.getControl(form, "voteThreshold") as AbstractControl<string>;
  }

  getVoteMissionPlayersControl(form: FormGroup) {
    return this.getControl(form, "voteMissionPlayers") as AbstractControl<number>;
  }

  private getControl(form: FormGroup, controlName: string) {
    return form.get(controlName)!;
  }
}
