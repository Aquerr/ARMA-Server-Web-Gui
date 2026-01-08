import { TestBed } from "@angular/core/testing";

import { ModSettingsService } from "./mod-settings.service";
import { provideHttpClientTesting } from "@angular/common/http/testing";

describe("ModSettingsService", () => {
  let service: ModSettingsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(ModSettingsService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
