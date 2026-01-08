import { TestBed } from "@angular/core/testing";

import { DiscordSettingsService } from "./discord-settings.service";
import { provideHttpClientTesting } from "@angular/common/http/testing";

describe("DiscordSettingsService", () => {
  let service: DiscordSettingsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(DiscordSettingsService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
