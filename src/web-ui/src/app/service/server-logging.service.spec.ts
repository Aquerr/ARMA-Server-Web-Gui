import { TestBed } from "@angular/core/testing";

import { ServerLoggingService } from "./server-logging.service";

describe("ServerLoggingService", () => {
  let service: ServerLoggingService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ServerLoggingService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
