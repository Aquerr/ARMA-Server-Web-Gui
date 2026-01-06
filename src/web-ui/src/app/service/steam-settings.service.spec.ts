import { TestBed } from "@angular/core/testing";

import { SteamSettingsService } from "./steam-settings.service";

describe("SteamSettingsService", () => {
  let service: SteamSettingsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SteamSettingsService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
