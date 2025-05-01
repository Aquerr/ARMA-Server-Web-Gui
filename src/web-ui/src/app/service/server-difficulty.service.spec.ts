import { TestBed } from "@angular/core/testing";

import { ServerDifficultyService } from "./server-difficulty.service";

describe("DifficultyService", () => {
  let service: ServerDifficultyService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ServerDifficultyService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
