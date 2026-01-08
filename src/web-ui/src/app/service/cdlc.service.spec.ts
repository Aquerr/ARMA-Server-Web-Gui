import { TestBed } from "@angular/core/testing";

import { CdlcService } from "./cdlc.service";
import { provideHttpClientTesting } from "@angular/common/http/testing";

describe("CdlcService", () => {
  let service: CdlcService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(CdlcService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
