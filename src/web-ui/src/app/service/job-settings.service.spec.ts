import { TestBed } from "@angular/core/testing";

import { JobSettingsService } from "./job-settings.service";
import { provideHttpClientTesting } from "@angular/common/http/testing";

describe("JobSettingsService", () => {
  let service: JobSettingsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(JobSettingsService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
