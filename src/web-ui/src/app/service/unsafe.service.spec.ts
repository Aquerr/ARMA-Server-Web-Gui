import { TestBed } from "@angular/core/testing";

import { UnsafeService } from "./unsafe.service";
import { provideHttpClientTesting } from "@angular/common/http/testing";

describe("UnsafeService", () => {
  let service: UnsafeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(UnsafeService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
