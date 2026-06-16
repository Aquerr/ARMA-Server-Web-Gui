import { TestBed } from "@angular/core/testing";

import { FileUploadMonitorService } from "./file-upload-monitor.service";

describe("FileUploadMonitorService", () => {
  let service: FileUploadMonitorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: []
    });
    service = TestBed.inject(FileUploadMonitorService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
