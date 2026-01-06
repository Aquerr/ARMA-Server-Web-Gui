import { TestBed } from "@angular/core/testing";

import { JobSettingsService } from "./job-settings.service";

describe("JobSettingsService", () => {
  let service: JobSettingsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(JobSettingsService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
