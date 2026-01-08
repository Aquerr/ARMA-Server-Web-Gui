import { vi } from "vitest";

export class ServerSecurityServiceMock {
  getServerSecurity = vi.fn();
  saveServerSecurity = vi.fn();
}
