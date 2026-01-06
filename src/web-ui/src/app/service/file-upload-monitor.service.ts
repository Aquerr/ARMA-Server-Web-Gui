import { inject, Injectable } from "@angular/core";
import { MatSnackBar, MatSnackBarRef } from "@angular/material/snack-bar";
import { UploadingFile } from "./file-upload.service";
import { FileUploadSnackBarComponent } from "../common-ui/file-upload-snack-bar/file-upload-snack-bar.component";
import { HttpErrorResponse, HttpEvent, HttpEventType, HttpProgressEvent } from "@angular/common/http";
import { interval, Observer, Subject, Subscription } from "rxjs";
import { NotificationService } from "./notification.service";
import { ApiErrorResponse } from "../api/api-error.model";

@Injectable({
  providedIn: "root"
})
export class FileUploadMonitorService {
  uploadingFilesChanged: Subject<void> = new Subject<void>();
  uploadingFiles: UploadingFile[] = [];

  private matSnackBar: MatSnackBar;
  private notificationService: NotificationService;

  private fileUploadSnackBarRef!: MatSnackBarRef<FileUploadSnackBarComponent> | null;

  private fileUploadedSubject!: Subject<File | null>;
  private fileUploadSubscription!: Subscription;

  private canDisplayFileUploadSubscription!: Subscription;

  constructor() {
    this.matSnackBar = inject(MatSnackBar);
    this.notificationService = inject(NotificationService);

    this.fileUploadedSubject = new Subject();

    this.fileUploadSubscription = this.fileUploadedSubject.subscribe(() => {
      if (this.getUploadingFiles().length == 0) {
        this.fileUploadSnackBarRef?.dismiss();
        this.fileUploadSnackBarRef = null;
      }
    });

    this.canDisplayFileUploadSubscription = interval(2000)
      .pipe()
      .subscribe(() => {
        if (this.uploadingFiles.length > 0 && this.fileUploadSnackBarRef == null) {
          // TODO: Show the ability to open file upload progress snack bar

        }
      });
  }

  getUploadingFiles() {
    return this.uploadingFiles;
  }

  isInQueue(file: File) {
    return this.uploadingFiles.find(
      (search) => search.fileName === file.name && search.totalSize === file.size
    );
  }

  monitorFileUpload(file: File): Observer<HttpProgressEvent | HttpEvent<object>> {
    this.uploadingFiles.push({
      fileName: file.name,
      progress: 0,
      uploadedSize: 0,
      totalSize: file.size
    } as UploadingFile);
    this.showUploadProgressSnackBar();

    return {
      next: (response: HttpProgressEvent | HttpEvent<object>) => {
        if (response.type == HttpEventType.UploadProgress) {
          const uploadingFile = this.uploadingFiles.find(
            (uploadingFile) => uploadingFile.fileName === file.name
          );
          if (uploadingFile) {
            uploadingFile.uploadedSize = response.loaded;
            uploadingFile.progress = Math.round((response.loaded / (response.total ?? 1)) * 100);
            this.uploadingFilesChanged.next();

            if (uploadingFile.shouldCancel) {
              this.removeFileWithName(uploadingFile.fileName);
              this.fileUploadedSubject.next(null);
              this.notificationService.warningNotification("File upload cancelled.");
              throw Error("File upload cancelled!");
            }
          }
        }

        if (response.type == HttpEventType.Response) {
          this.removeFileWithName(file.name);
          console.log("Response:", response);
          this.fileUploadedSubject.next(file);
        }
      },
      error: (error: HttpErrorResponse) => {
        this.removeFileWithName(file.name);
        const apiErrorResponse = error.error as ApiErrorResponse;
        this.notificationService.errorNotification(apiErrorResponse.message);
        this.fileUploadedSubject.next(null);
      },
      complete: () => {
        this.removeFileWithName(file.name);
        this.fileUploadedSubject.next(file);
      }
    };
  }

  showUploadProgressSnackBar() {
    if (!this.fileUploadSnackBarRef) {
      console.log("Opening file upload...");
      this.fileUploadSnackBarRef = this.matSnackBar.openFromComponent(FileUploadSnackBarComponent, {
        data: this
      });
      this.fileUploadSnackBarRef.afterDismissed().subscribe(() => {
        this.fileUploadSnackBarRef = null;
      });
    }
  }

  private removeFileWithName(name: string) {
    const uploadingFileIndex = this.uploadingFiles.findIndex(
      (uploadingFile) => uploadingFile.fileName === name
    );
    if (uploadingFileIndex != -1) {
      this.uploadingFiles.splice(uploadingFileIndex, 1);
      this.uploadingFilesChanged.next();
    }
  }
}
