import { TestBed } from "@angular/core/testing";

import { DiscordSettingsService } from "./discord-settings.service";

describe("DiscordSettingsService", () => {
  let service: DiscordSettingsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DiscordSettingsService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
