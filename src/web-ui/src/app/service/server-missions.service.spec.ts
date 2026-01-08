import { TestBed } from "@angular/core/testing";

import { ServerMissionsService } from "./server-missions.service";
import { provideHttpClientTesting } from "@angular/common/http/testing";

describe("ServerMissionsService", () => {
  let service: ServerMissionsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(ServerMissionsService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
