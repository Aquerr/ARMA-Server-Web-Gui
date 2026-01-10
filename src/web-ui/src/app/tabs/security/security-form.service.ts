import { Injectable } from "@angular/core";
import { AbstractControl, FormBuilder, FormControl, FormGroup, Validators } from "@angular/forms";
import {
  AllowedFilePatching,
  GetServerSecurityResponse,
  SaveServerSecurityRequest
} from "../../service/server-security.service";
import { CommandListItem } from "./vote-cmds-list/vote-cmd-list-item/vote-cmd-list-item.model";

export interface SecurityFormControls {
  serverPassword: FormControl<string>;
  serverAdminPassword: FormControl<string>;
  serverCommandPassword: FormControl<string>;
  battleEye: FormControl<boolean>;
  verifySignatures: FormControl<boolean>;
  allowedFilePatching: FormControl<AllowedFilePatching>;
  filePatchingIgnoredClients: FormControl<string[]>;
  allowedLoadFileExtensions: FormControl<string[]>;
  allowedPreprocessFileExtensions: FormControl<string[]>;
  allowedHTMLLoadExtensions: FormControl<string[]>;
  adminUUIDs: FormControl<string[]>;
  allowedVoteCmds: FormControl<CommandListItem[]>;
  kickDuplicate: FormControl<boolean>;
  voteThreshold: FormControl<string>;
  voteMissionPlayers: FormControl<number>;
}

@Injectable({
  providedIn: "root"
})
export class SecurityFormService {
  constructor(private readonly fb: FormBuilder) {}

  getForm(): FormGroup<SecurityFormControls> {
    return this.fb.nonNullable.group({
      serverPassword: this.fb.nonNullable.control(""),
      serverAdminPassword: this.fb.nonNullable.control(""),
      serverCommandPassword: this.fb.nonNullable.control(""),
      battleEye: [true, [Validators.required]],
      verifySignatures: [true, [Validators.required]],
      allowedFilePatching: [AllowedFilePatching.NOT_ALLOWED, [Validators.required]],
      filePatchingIgnoredClients: this.fb.nonNullable.control<string[]>([]),
      allowedLoadFileExtensions: this.fb.nonNullable.control<string[]>([]),
      allowedPreprocessFileExtensions: this.fb.nonNullable.control<string[]>([]),
      allowedHTMLLoadExtensions: this.fb.nonNullable.control<string[]>([]),
      adminUUIDs: this.fb.nonNullable.control<string[]>([]),
      allowedVoteCmds: this.fb.nonNullable.control<CommandListItem[]>([]),
      kickDuplicate: [false, [Validators.required]],
      voteThreshold: ["0.5", [Validators.required]],
      voteMissionPlayers: [1, [Validators.required]]
    });
  }

  setForm(form: FormGroup<SecurityFormControls>, data: GetServerSecurityResponse) {
    form.controls.serverPassword.patchValue(data.serverPassword);
    form.controls.serverAdminPassword.patchValue(data.serverAdminPassword);
    form.controls.serverCommandPassword.patchValue(data.serverCommandPassword);
    form.controls.battleEye.patchValue(data.battleEye);
    form.controls.verifySignatures.patchValue(data.verifySignatures);
    form.controls.allowedFilePatching.patchValue(data.allowedFilePatching);
    form.controls.filePatchingIgnoredClients.patchValue(data.filePatchingIgnoredClients);
    form.controls.allowedLoadFileExtensions.patchValue(data.allowedLoadFileExtensions);
    form.controls.allowedPreprocessFileExtensions.patchValue(data.allowedPreprocessFileExtensions);
    form.controls.allowedHTMLLoadExtensions.patchValue(data.allowedHTMLLoadExtensions);
    form.controls.adminUUIDs.patchValue(data.adminUUIDs);
    form.controls.allowedVoteCmds.patchValue(data.allowedVoteCmds.map((voteCmd) => new CommandListItem(voteCmd)));
    form.controls.kickDuplicate.patchValue(data.kickDuplicate);
    form.controls.voteThreshold.patchValue(data.voteThreshold);
    form.controls.voteMissionPlayers.patchValue(data.voteMissionPlayers);
  }

  get(form: FormGroup<SecurityFormControls>) {
    return {
      serverPassword: this.getServerPasswordControl(form).value,
      serverAdminPassword: this.getServerAdminPasswordControl(form).value,
      serverCommandPassword: this.getServerCommandPasswordControl(form).value,
      battleEye: this.getBattleEyeControl(form).value,
      verifySignatures: this.getVerifySignaturesControl(form).value,
      allowedFilePatching: this.getAllowedFilePatchingControl(form).value,
      filePatchingIgnoredClients: this.getFilePatchingIgnoredClientsControl(form).value,
      allowedLoadFileExtensions: this.getAllowedLoadFileExtensionsControl(form).value,
      allowedPreprocessFileExtensions: form.controls.allowedPreprocessFileExtensions.value,
      allowedHTMLLoadExtensions: form.controls.allowedHTMLLoadExtensions.value,
      adminUUIDs: this.getAdminUUIDsControl(form).value,
      allowedVoteCmds: form.controls.allowedVoteCmds.value.map((commandListItem) => commandListItem.command),
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
