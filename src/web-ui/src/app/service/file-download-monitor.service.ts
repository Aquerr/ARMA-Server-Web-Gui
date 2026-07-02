import { inject, Service } from "@angular/core";
import { Observer, Subject, Subscription } from "rxjs";
import { MatSnackBar, MatSnackBarRef } from "@angular/material/snack-bar";
import { NotificationService } from "@service/notification.service";
import {
  HttpDownloadProgressEvent,
  HttpErrorResponse,
  HttpEvent,
  HttpEventType,
  HttpResponse
} from "@angular/common/http";
import { ApiErrorResponse } from "@app/api/api-error.model";
import { FileDownloadSnackBarComponent } from "@common-ui/file-download-snack-bar/file-download-snack-bar.component";

@Service()
export class FileDownloadMonitorService {
  downloadingFilesChanged: Subject<void> = new Subject<void>();
  downloadingFiles: DownloadingFile[] = [];

  private matSnackBar: MatSnackBar;
  private notificationService: NotificationService;

  private fileDownloadSnackBarRef!: MatSnackBarRef<FileDownloadSnackBarComponent> | null;

  private fileDownloadedSubject!: Subject<string | null>;
  private fileDownloadSubscription!: Subscription;

  private canDisplayFileDownloadSubscription!: Subscription;

  constructor() {
    this.matSnackBar = inject(MatSnackBar);
    this.notificationService = inject(NotificationService);

    this.fileDownloadedSubject = new Subject();

    this.fileDownloadSubscription = this.fileDownloadedSubject.subscribe(() => {
      if (this.getDownloadingFiles().length == 0) {
        this.fileDownloadSnackBarRef?.dismiss();
        this.fileDownloadSnackBarRef = null;
      }
    });

    if (this.downloadingFiles.length > 0 && this.fileDownloadSnackBarRef == null) {
      this.showDownloadProgressSnackBar();
    }
  }

  getDownloadingFiles() {
    return this.downloadingFiles;
  }

  isInQueue(fileName: string) {
    return this.downloadingFiles.find((search) => search.fileName === fileName);
  }

  monitorFileDownload(fileName: string): Observer<HttpDownloadProgressEvent | HttpEvent<Blob>> {
    this.downloadingFiles.push({
      fileName,
      progress: 0,
      downloadedSize: 0,
      totalSize: 0
    });
    this.downloadingFilesChanged.next();
    this.showDownloadProgressSnackBar();

    return {
      next: (response: HttpDownloadProgressEvent | HttpEvent<Blob>) => {
        if (response.type == HttpEventType.DownloadProgress) {
          const downloadingFile = this.downloadingFiles.find(
            (downloadingFile) => downloadingFile.fileName === fileName
          );
          if (downloadingFile) {
            downloadingFile.downloadedSize = response.loaded;
            downloadingFile.totalSize = response.total ?? downloadingFile.totalSize;
            downloadingFile.progress = downloadingFile.totalSize
              ? Math.round((response.loaded / downloadingFile.totalSize) * 100)
              : 0;
            this.downloadingFilesChanged.next();

            if (downloadingFile.shouldCancel) {
              this.removeFileWithName(fileName);
              this.fileDownloadedSubject.next(null);
              this.notificationService.warningNotification("File download cancelled.");
              throw Error("File download cancelled!");
            }
          }
        }

        if (response.type == HttpEventType.Response) {
          this.saveBlobResponse(response, fileName);
          this.removeFileWithName(fileName);
          this.fileDownloadedSubject.next(fileName);
        }
      },
      error: (error: HttpErrorResponse) => {
        this.removeFileWithName(fileName);
        const apiErrorResponse = error.error as ApiErrorResponse;
        this.notificationService.errorNotification(apiErrorResponse?.message ?? "Download failed.");
        this.fileDownloadedSubject.next(null);
      },
      complete: () => {
        // Response branch above already handles cleanup/save;
        // complete fires after next(), so avoid double-removal here.
      }
    };
  }

  private saveBlobResponse(response: HttpResponse<Blob>, fallbackFileName: string): void {
    const blob: Blob | null = response.body;
    const contentDisposition = response.headers?.get?.("Content-Disposition");
    const extractedName = this.extractFileName(contentDisposition) ?? fallbackFileName;

    const url = window.URL.createObjectURL(blob!);
    const a = document.createElement("a");
    a.href = url;
    a.download = extractedName;
    a.click();
    window.URL.revokeObjectURL(url);
  }

  private extractFileName(contentDisposition: string | null): string | null {
    if (!contentDisposition) return null;
    const match = /filename\*?=(?:UTF-8'')?["']?([^"';]+)["']?/i.exec(contentDisposition);
    return match ? decodeURIComponent(match[1]) : null;
  }

  public isSnackBarOpen(): boolean {
    return this.fileDownloadSnackBarRef != null;
  }

  showDownloadProgressSnackBar() {
    if (!this.fileDownloadSnackBarRef) {
      this.fileDownloadSnackBarRef = this.matSnackBar.openFromComponent(FileDownloadSnackBarComponent, {
        data: this
      });
      this.fileDownloadSnackBarRef.afterDismissed().subscribe(() => {
        this.fileDownloadSnackBarRef = null;
        this.downloadingFilesChanged.next();
      });
    }
  }

  private removeFileWithName(name: string) {
    const downloadingFileIndex = this.downloadingFiles.findIndex(
      (downloadingFile) => downloadingFile.fileName === name
    );
    if (downloadingFileIndex != -1) {
      this.downloadingFiles.splice(downloadingFileIndex, 1);
      this.downloadingFilesChanged.next();
    }
  }
}

export interface DownloadingFile {
  fileName: string;
  progress: number;
  downloadedSize: number;
  totalSize: number;
  shouldCancel?: boolean;
}
