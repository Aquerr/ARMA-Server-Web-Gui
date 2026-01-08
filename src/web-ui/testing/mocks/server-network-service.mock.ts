import { vi } from "vitest";

export class ServerNetworkServiceMock {
  getServerNetworkProperties = vi.fn();
  saveServerNetworkProperties = vi.fn();
}
