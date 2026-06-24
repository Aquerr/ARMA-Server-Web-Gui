import { Injectable } from "@angular/core";
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from "@angular/forms";
import {
  AllowedFilePatching,
  GetServerSecurityResponse
} from "@service/server-security.service";
import { VoteCmd } from "@model/vote-cmd.model";

export interface VoteCmdFormGroupControls {
  name: FormControl<string>;
  allowedPreMission: FormControl<boolean>;
  allowedPostMission: FormControl<boolean>;
  votingThreshold: FormControl<number>;
  percentageSideVotingThreshold: FormControl<number>;
}

export interface VoteCmdFormGroupWrapperControls {
  command: FormGroup<VoteCmdFormGroupControls>;
  editing: FormControl<boolean>;
}

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
  allowedVoteCmds: FormArray<FormGroup<VoteCmdFormGroupWrapperControls>>;
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
      allowedVoteCmds: this.fb.nonNullable.array<FormGroup<VoteCmdFormGroupWrapperControls>>([]),
      kickDuplicate: [false, [Validators.required]],
      voteThreshold: ["0.5", [Validators.required]],
      voteMissionPlayers: [1, [Validators.required]]
    });
  }

  setForm(form: FormGroup<SecurityFormControls>, data: GetServerSecurityResponse) {
    form.patchValue({
      serverPassword: data.serverPassword,
      serverAdminPassword: data.serverAdminPassword,
      serverCommandPassword: data.serverCommandPassword,
      battleEye: data.battleEye,
      verifySignatures: data.verifySignatures,
      allowedFilePatching: data.allowedFilePatching,
      filePatchingIgnoredClients: data.filePatchingIgnoredClients,
      allowedLoadFileExtensions: data.allowedLoadFileExtensions,
      allowedPreprocessFileExtensions: data.allowedPreprocessFileExtensions,
      allowedHTMLLoadExtensions: data.allowedHTMLLoadExtensions,
      adminUUIDs: data.adminUUIDs,
      kickDuplicate: data.kickDuplicate,
      voteThreshold: data.voteThreshold,
      voteMissionPlayers: data.voteMissionPlayers
    });

    form.controls.allowedVoteCmds.clear();
    data.allowedVoteCmds.map((voteCmd) => {
      return {
        command: voteCmd,
        editing: false
      };
    }).forEach((voteCmd) => {
      const formGroup = this.fb.group<VoteCmdFormGroupWrapperControls>({
        command: this.fb.nonNullable.group({
          name: this.fb.nonNullable.control("undefined"),
          allowedPreMission: this.fb.nonNullable.control<boolean>(false),
          allowedPostMission: this.fb.nonNullable.control<boolean>(false),
          votingThreshold: this.fb.nonNullable.control<number>(0),
          percentageSideVotingThreshold: this.fb.nonNullable.control<number>(0)
        }),
        editing: this.fb.nonNullable.control(false)
      });
      formGroup.setValue(voteCmd);
      form.controls.allowedVoteCmds.push(formGroup);
    });

    form.updateValueAndValidity();
  }

  get(form: FormGroup<SecurityFormControls>) {
    const formValue = form.getRawValue();

    return {
      serverPassword: formValue.serverPassword,
      serverAdminPassword: formValue.serverAdminPassword,
      serverCommandPassword: formValue.serverCommandPassword,
      battleEye: formValue.battleEye,
      verifySignatures: formValue.verifySignatures,
      allowedFilePatching: formValue.allowedFilePatching,
      filePatchingIgnoredClients: formValue.filePatchingIgnoredClients,
      allowedLoadFileExtensions: formValue.allowedLoadFileExtensions,
      allowedPreprocessFileExtensions: formValue.allowedPreprocessFileExtensions,
      allowedHTMLLoadExtensions: formValue.allowedHTMLLoadExtensions,
      adminUUIDs: formValue.adminUUIDs,
      allowedVoteCmds: formValue.allowedVoteCmds.map((item) => item.command).map((command) => {
        return {
          name: command.name,
          allowedPreMission: command.allowedPreMission,
          allowedPostMission: command.allowedPostMission,
          votingThreshold: command.votingThreshold,
          percentageSideVotingThreshold: command.percentageSideVotingThreshold
        } satisfies VoteCmd;
      }),
      kickDuplicate: formValue.kickDuplicate,
      voteThreshold: formValue.voteThreshold,
      voteMissionPlayers: formValue.voteMissionPlayers
    };
  }
}
