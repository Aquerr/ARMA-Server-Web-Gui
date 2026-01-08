import { TestBed } from "@angular/core/testing";

import { ModUploadService } from "./mod-upload.service";
import { provideToastr } from "ngx-toastr";

describe("ModUploadService", () => {
  let service: ModUploadService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideToastr()]
    });
    service = TestBed.inject(ModUploadService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
