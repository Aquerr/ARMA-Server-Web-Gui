export interface Mod {
  workshopFileId: number;
  name: string;
  serverMod: boolean;
  previewUrl: string;
  workshopUrl: string;
  fileExists: boolean;
  lastWorkshopUpdateDateTime: string;
  sizeBytes: number;
}

export interface NotManagedMod extends Mod {
  directoryName: string;
}

export interface ModPresetEntry {
  id: number;
  name: string;
}

export interface ModPreset {
  name: string;
  entries: ModPresetEntry[];
}
