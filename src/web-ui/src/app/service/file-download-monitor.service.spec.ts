import { TestBed } from "@angular/core/testing";
import { FileDownloadMonitorService } from "@service/file-download-monitor.service";

describe("FileDownloadMonitorService", () => {
  let service: FileDownloadMonitorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: []
    });
    service = TestBed.inject(FileDownloadMonitorService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
