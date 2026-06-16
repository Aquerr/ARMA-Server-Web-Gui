import { TestBed } from "@angular/core/testing";

import { FileUploadService } from "./file-upload.service";
import { Observable, of } from "rxjs";
import { HttpEvent } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { NotificationService } from "./notification.service";

@Injectable()
class MockFileUploadService extends FileUploadService {
  constructor(notificationService: NotificationService) {
    super(notificationService, ["application/zip"], [".zip"]);
  }

  protected doUpload(): Observable<HttpEvent<object>> {
    return of();
  }
}

describe("FileUploadService", () => {
  let service: FileUploadService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: FileUploadService,
          useClass: MockFileUploadService
        }
      ]
    });
    service = TestBed.inject(FileUploadService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
