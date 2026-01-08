import { TestBed } from "@angular/core/testing";

import { ServerModsService } from "./server-mods.service";
import { provideHttpClientTesting } from "@angular/common/http/testing";

describe("ServerModsService", () => {
  let service: ServerModsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(ServerModsService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
