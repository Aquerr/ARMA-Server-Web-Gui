import { TestBed } from "@angular/core/testing";

import { ApplicationService } from "./application.service";
import { provideHttpClientTesting } from "@angular/common/http/testing";

describe("ApplicationService", () => {
  let service: ApplicationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(ApplicationService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
