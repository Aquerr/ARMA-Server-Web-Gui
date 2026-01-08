import { vi } from "vitest";

export class DiscordSettingsServiceMock {
  getDiscordSettings = vi.fn();
  saveDiscordSettings = vi.fn();
}
