import { TestBed } from "@angular/core/testing";

import { ServerGeneralService } from "./server-general.service";

describe("ServerGeneralService", () => {
  let service: ServerGeneralService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ServerGeneralService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
