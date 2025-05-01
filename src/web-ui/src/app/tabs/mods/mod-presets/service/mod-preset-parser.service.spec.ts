import { TestBed } from "@angular/core/testing";

import { ModPresetParserService } from "./mod-preset-parser.service";

describe("ModPresetParserService", () => {
  let service: ModPresetParserService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ModPresetParserService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
