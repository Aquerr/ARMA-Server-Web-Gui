import { TestBed } from "@angular/core/testing";

import { ModSettingsService } from "./mod-settings.service";

describe("ModSettingsService", () => {
  let service: ModSettingsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ModSettingsService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
