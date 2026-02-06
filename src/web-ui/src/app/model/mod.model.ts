export interface Mod {
  workshopFileId: number;
  name: string;
  serverMod: boolean;
  previewUrl: string;
  workshopUrl: string;
  status: ModStatus;
  lastWorkshopUpdateDateTime: string;
  lastWorkshopUpdateAttemptDateTime: string;
  sizeBytes: number;
  directoryName: string;
}

export enum ModStatus {
  INSTALLING = "INSTALLING",
  MISSING_DEPENDENCY_MODS = "MISSING_DEPENDENCY_MODS",
  MISSING_FILES = "MISSING_FILES",
  READY = "READY"
}

export interface ModPresetEntry {
  id: number;
  name: string;
}

export interface ModPreset {
  name: string | null | undefined;
  entries: ModPresetEntry[];
}
