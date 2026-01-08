import { vi } from "vitest";

export class ServerGeneralServiceMock {
  getGeneralProperties = vi.fn();
  saveGeneralProperties = vi.fn();
}
