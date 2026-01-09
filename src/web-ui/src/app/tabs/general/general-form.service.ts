import { inject, Injectable } from "@angular/core";
import { FormBuilder, FormControl, FormGroup, Validators } from "@angular/forms";
import { MissionDifficulty } from "../../model/mission.model";
import { GeneralProperties } from "../../service/server-general.service";

export class MotdItem {
  message: string = "";
  editing: boolean = false;

  constructor(message: string) {
    this.message = message;
  }
}

export interface GeneralFormGroup {
  commandLineParams: FormControl<string>;
  canOverwriteCommandLineParams: FormControl<boolean>;
  serverDirectory: FormControl<string>;
  modsDirectory: FormControl<string>;
  hostname: FormControl<string>;
  port: FormControl<number>;
  maxPlayers: FormControl<number>;
  motd: FormControl<MotdItem[]>;
  motdInterval: FormControl<number>;
  persistent: FormControl<boolean>;
  drawingInMap: FormControl<boolean>;
  headlessClients: FormControl<string[]>;
  localClients: FormControl<string[]>;
  forcedDifficulty: FormControl<MissionDifficulty | null>;
  branch: FormControl<string>;
}

@Injectable({
  providedIn: "root"
})
export class GeneralFormService {
  private readonly fb = inject(FormBuilder);

  createForm(): FormGroup<GeneralFormGroup> {
    return this.fb.nonNullable.group({
      commandLineParams: ["", [Validators.required]],
      canOverwriteCommandLineParams: [false, [Validators.required]],
      serverDirectory: ["", [Validators.required]],
      modsDirectory: [""],
      hostname: ["", [Validators.required]],
      port: [2302, [Validators.required]],
      maxPlayers: [64, [Validators.required]],
      motd: this.fb.nonNullable.control<MotdItem[]>([]),
      motdInterval: this.fb.nonNullable.control<number>(5, Validators.required),
      persistent: [false, [Validators.required]],
      drawingInMap: [true, [Validators.required]],
      headlessClients: this.fb.nonNullable.control<string[]>([]),
      localClients: this.fb.nonNullable.control<string[]>([]),
      forcedDifficulty: this.fb.control<MissionDifficulty | null>(null),
      branch: ["public", [Validators.required]]
    });
  }

  setForm(form: GeneralFormGroup, data: GeneralProperties) {
    form.serverDirectory.patchValue(data.serverDirectory);
    form.modsDirectory.patchValue(data.modsDirectory);
    form.commandLineParams.patchValue(data.commandLineParams);
    form.canOverwriteCommandLineParams.patchValue(data.canOverwriteCommandLineParams);
    form.port.patchValue(data.port);
    form.hostname.patchValue(data.hostname);
    form.maxPlayers.patchValue(data.maxPlayers);
    form.motd.patchValue(data.motd.map((message) => new MotdItem(message)));
    form.motdInterval.patchValue(data.motdInterval);
    form.persistent.patchValue(data.persistent);
    form.drawingInMap.patchValue(data.drawingInMap);
    form.headlessClients.patchValue(data.headlessClients);
    form.localClients.patchValue(data.localClients);
    form.forcedDifficulty.patchValue(data.forcedDifficulty);
    form.branch.patchValue(data.branch);
  }

  asGeneralProperties(form: GeneralFormGroup) {
    return {
      serverDirectory: form.serverDirectory.value,
      modsDirectory: form.modsDirectory.value,
      commandLineParams: form.commandLineParams.value,
      canOverwriteCommandLineParams: form.canOverwriteCommandLineParams.value,
      port: form.port.value,
      hostname: form.hostname.value,
      maxPlayers: form.maxPlayers.value,
      motd: form.motd.value.map((item) => item.message),
      motdInterval: form.motdInterval.value,
      persistent: form.persistent.value,
      drawingInMap: form.drawingInMap.value,
      headlessClients: form.headlessClients.value,
      localClients: form.localClients.value,
      forcedDifficulty: form.forcedDifficulty.value,
      branch: form.branch.value
    } as GeneralProperties;
  }
}
