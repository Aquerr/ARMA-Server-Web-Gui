import { TestBed } from "@angular/core/testing";

import { WorkshopService } from "./workshop.service";
import { provideHttpClientTesting } from "@angular/common/http/testing";

describe("WorkshopService", () => {
  let service: WorkshopService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(WorkshopService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
