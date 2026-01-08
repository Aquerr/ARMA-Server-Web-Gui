import { TestBed } from "@angular/core/testing";

import { SteamSettingsService } from "./steam-settings.service";
import { provideHttpClientTesting } from "@angular/common/http/testing";

describe("SteamSettingsService", () => {
  let service: SteamSettingsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(SteamSettingsService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
