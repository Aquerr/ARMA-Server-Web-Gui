import { vi } from "vitest";

export class CdlcServiceMock {
  getAllCdlcs = vi.fn();
  toggleCdlc = vi.fn();
}
