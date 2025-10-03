import { TestBed } from "@angular/core/testing";

import { LoadingSpinnerMaskService } from "./loading-spinner-mask.service";

describe("MaskService", () => {
  let service: LoadingSpinnerMaskService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LoadingSpinnerMaskService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
