import { vi } from "vitest";

export class ServerStatusServiceMock {
  getStatus = vi.fn();
  toggleServer = vi.fn();
}
