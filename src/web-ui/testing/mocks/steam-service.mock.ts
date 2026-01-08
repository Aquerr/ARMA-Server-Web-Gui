import { vi } from "vitest";

export class SteamServiceMock {
  getSteamSettings = vi.fn();
  saveSteamSettings = vi.fn();
  updateSteamPassword = vi.fn();
}
