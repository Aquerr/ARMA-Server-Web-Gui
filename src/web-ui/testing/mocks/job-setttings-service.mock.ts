import { vi } from "vitest";

export class JobSetttingsServiceMock {
  getJobSettings = vi.fn();
  getAllJobsNames = vi.fn();
}
