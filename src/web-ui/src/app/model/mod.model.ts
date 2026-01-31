export interface Mod {
  workshopFileId: number;
  name: string;
  serverMod: boolean;
  previewUrl: string;
  workshopUrl: string;
  fileExists: boolean;
  lastWorkshopUpdateDateTime: string;
  lastWorkshopUpdateAttemptDateTime: string;
  sizeBytes: number;
  directoryName: string;
}

export interface ModPresetEntry {
  id: number;
  name: string;
}

export interface ModPreset {
  name: string | null | undefined;
  entries: ModPresetEntry[];
}
