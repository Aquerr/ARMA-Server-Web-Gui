import { TestBed } from "@angular/core/testing";

import { FileUploadMonitorService } from "./file-upload-monitor.service";
import { provideToastr } from "ngx-toastr";

describe("FileUploadMonitorService", () => {
  let service: FileUploadMonitorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideToastr()]
    });
    service = TestBed.inject(FileUploadMonitorService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
