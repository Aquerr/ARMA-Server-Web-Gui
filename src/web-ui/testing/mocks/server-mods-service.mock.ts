import { vi } from "vitest";

export class ServerModsServiceMock {
  uploadMod = vi.fn();
  checkModFilesExists = vi.fn();
  getInstalledMods = vi.fn();
  deleteMod = vi.fn();
  saveEnabledMods = vi.fn();
  manageMod = vi.fn();
  getModPresetsNames = vi.fn();
  importPreset = vi.fn();
  selectPreset = vi.fn();
  deletePreset = vi.fn();
  savePreset = vi.fn();
  deleteNotManagedMod = vi.fn();
}
