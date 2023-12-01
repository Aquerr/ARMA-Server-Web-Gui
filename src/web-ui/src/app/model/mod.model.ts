export interface Mod {
  workshopFileId: number;
  name: string;
  serverMod: boolean;
  previewUrl: string;
  workshopUrl: string;
  fileExists: boolean;
}

export interface ModPresetEntry {
  id: number;
  name: string;
}

export interface ModPreset {
  name: string;
  entries: ModPresetEntry[]
}
