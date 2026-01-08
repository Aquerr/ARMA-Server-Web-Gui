import { vi } from "vitest";

export class WorkshopServiceMock {
  getModDownloadQueue = vi.fn();
  canUseWorkshop = vi.fn();
  getInstalledWorkshopItems = vi.fn();
  queryWorkshop = vi.fn();
  installMod = vi.fn();
}
