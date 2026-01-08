import { TestBed } from "@angular/core/testing";

import { ServerDifficultyService } from "./server-difficulty.service";
import { provideHttpClientTesting } from "@angular/common/http/testing";

describe("DifficultyService", () => {
  let service: ServerDifficultyService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(ServerDifficultyService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
