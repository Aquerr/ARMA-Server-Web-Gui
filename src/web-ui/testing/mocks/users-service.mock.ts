import { vi } from "vitest";

export class UsersServiceMock {
  getUsers = vi.fn();
}
