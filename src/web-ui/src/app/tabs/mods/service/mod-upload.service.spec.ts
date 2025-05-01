import { TestBed } from "@angular/core/testing";

import { ModUploadService } from "./mod-upload.service";

describe("ModUploadService", () => {
  let service: ModUploadService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ModUploadService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
