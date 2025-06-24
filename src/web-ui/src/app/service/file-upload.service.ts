import { inject, Injectable } from "@angular/core";
import { Observable, Subject, tap } from "rxjs";
import { NotificationService } from "./notification.service";
import { FileUploadMonitorService } from "./file-upload-monitor.service";
import { HttpEventType } from "@angular/common/http";

@Injectable({
  providedIn: "root"
})
export abstract class FileUploadService {
  fileUploadMonitorService: FileUploadMonitorService;

  public fileUploadedSubject!: Subject<File | null>;

  protected constructor(
    protected notificationService: NotificationService,
    private allowedFileTypes: string[] = [],
    private allowedFileExtensions: string[] = []
  ) {
    this.fileUploadMonitorService = inject(FileUploadMonitorService);

    this.fileUploadedSubject = new Subject();
    this.fileUploadedSubject.subscribe((file) => {
      if (file) {
        this.doAfterUpload(file);
      }
    });
  }

  getUploadingFiles() {
    return this.fileUploadMonitorService.getUploadingFiles();
  }

  protected uploadFile(file: File, overwrite: boolean) {
    if (!this.isFileAllowed(file)) {
      this.notificationService.errorNotification(
        `Wrong file type! Only ${this.allowedFileExtensions} files are supported!`
      );
      return;
    }

    if (this.fileUploadMonitorService.isInQueue(file)) {
      this.notificationService.warningNotification(`Same file is already in upload queue!`);
      return;
    }

    return this.doUpload(file, overwrite)
      .pipe(tap(this.fileUploadMonitorService.monitorFileUpload(file)))
      .subscribe({
        next: (response) => {
          if (response.type == HttpEventType.Response) {
            this.fileUploadedSubject.next(file);
          }
        },
        error: (error) => {
          this.fileUploadedSubject.next(null);
        }
      });
  }

  private isFileAllowed(file: File): boolean {
    const fileName = file.name.toLowerCase();
    const fileExtension = fileName.substring(fileName.lastIndexOf("."));
    if (
      !this.allowedFileExtensions.includes("*") &&
      !this.allowedFileExtensions.includes(fileExtension)
    ) {
      return false;
    }

    return this.allowedFileTypes.includes("*") || this.allowedFileTypes.includes(file.type);
  }

  protected abstract doUpload(file: File, overwrite: boolean): Observable<any>;

  protected doAfterUpload(file: File | null) {}
}

export interface UploadingFile {
  fileName: string;
  progress: number;
  uploadedSize: number;
  totalSize: number;
  shouldCancel: boolean;
}
