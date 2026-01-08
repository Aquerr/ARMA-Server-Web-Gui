import { TestBed } from "@angular/core/testing";

import { ServerStatusService } from "./server-status.service";
import { provideHttpClientTesting } from "@angular/common/http/testing";

describe("ServerStatusService", () => {
  let service: ServerStatusService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(ServerStatusService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
