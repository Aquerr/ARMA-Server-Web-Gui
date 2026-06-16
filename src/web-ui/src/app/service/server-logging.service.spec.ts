import { TestBed } from "@angular/core/testing";

import { ServerLoggingService } from "./server-logging.service";
import { provideHttpClientTesting } from "@angular/common/http/testing";

describe("ServerLoggingService", () => {
  let service: ServerLoggingService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()]
    });
    service = TestBed.inject(ServerLoggingService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
