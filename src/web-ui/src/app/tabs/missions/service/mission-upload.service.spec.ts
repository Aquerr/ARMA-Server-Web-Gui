import { TestBed } from "@angular/core/testing";

import { MissionUploadService } from "./mission-upload.service";
import { provideToastr } from "ngx-toastr";

describe("MissionUploadService", () => {
  let service: MissionUploadService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideToastr()
      ]
    });
    service = TestBed.inject(MissionUploadService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
