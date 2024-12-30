import {inject, Injectable} from '@angular/core';
import {MatSnackBar, MatSnackBarRef} from "@angular/material/snack-bar";
import {UploadingFile} from "./file-upload.service";
import {FileUploadSnackBarComponent} from "../common-ui/file-upload-snack-bar/file-upload-snack-bar.component";
import { HttpEventType } from "@angular/common/http";
import {Observer, Subject, Subscription} from "rxjs";
import {NotificationService} from "./notification.service";

@Injectable({
  providedIn: 'root'
})
export class FileUploadMonitorService {

  private uploadingFiles: UploadingFile[] = [];

  private matSnackBar: MatSnackBar;
  private notificationService: NotificationService;

  private fileUploadSnackBarRef!: MatSnackBarRef<FileUploadSnackBarComponent> | null;

  private fileUploadedSubject!: Subject<File | null>;
  private fileUploadSubscription!: Subscription;

  constructor() {
    this.matSnackBar = inject(MatSnackBar);
    this.notificationService = inject(NotificationService);

    this.fileUploadedSubject = new Subject();

    this.fileUploadSubscription = this.fileUploadedSubject.subscribe((file) => {
      if (this.getUploadingFiles().length == 0) {
        this.fileUploadSnackBarRef?.dismiss();
        this.fileUploadSnackBarRef = null;
      }
    });
  }

  getUploadingFiles() {
    return this.uploadingFiles;
  }

  isInQueue(file: File) {
    return this.uploadingFiles.find(search => search.fileName === file.name && search.totalSize === file.size);
  }

  monitorFileUpload(file: any): Observer<any> {
    this.uploadingFiles.push({fileName: file.name, progress: 0, uploadedSize: 0, totalSize: file.size} as UploadingFile);
    this.showUploadProgressSnackBar();

    return {
      next: (response) => {
        if (response.type == HttpEventType.UploadProgress) {
          const uploadingFile = this.uploadingFiles.find(uploadingFile => uploadingFile.fileName === file.name);
          if (uploadingFile) {
            uploadingFile.uploadedSize = response.loaded;
            uploadingFile.progress = Math.round(response.loaded / response.total * 100);
          }
        }

        if (response.type == HttpEventType.Response) {
          this.removeFileWithName(file.name);
          this.fileUploadedSubject.next(file);
        }
      },
      error: (error) => {
        this.removeFileWithName(file.name);
        this.notificationService.errorNotification(error.error.message);
        this.fileUploadedSubject.next(null);
      },
      complete: () => {
        this.removeFileWithName(file.name);
        this.fileUploadedSubject.next(file);
      }
    }
  }

  showUploadProgressSnackBar() {
    if (!this.fileUploadSnackBarRef) {
      this.fileUploadSnackBarRef = this.matSnackBar.openFromComponent(
        FileUploadSnackBarComponent,
        {data: this}
      );
      this.fileUploadSnackBarRef.afterDismissed().subscribe(() => {
        this.fileUploadSnackBarRef = null;
      });
    }
  }


  private removeFileWithName(name: string) {
    const uploadingFileIndex = this.uploadingFiles.findIndex(uploadingFile => uploadingFile.fileName === name);
    if (uploadingFileIndex != -1) {
      this.uploadingFiles.splice(uploadingFileIndex, 1);
    }
  }
}
