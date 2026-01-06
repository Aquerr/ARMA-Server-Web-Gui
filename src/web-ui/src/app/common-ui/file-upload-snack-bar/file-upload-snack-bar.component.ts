import { ChangeDetectorRef, Component, inject, Inject, OnDestroy } from "@angular/core";
import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from "@angular/material/snack-bar";
import { UploadingFile } from "../../service/file-upload.service";
import { MatProgressBar } from "@angular/material/progress-bar";
import { FilesizePipe } from "../../util/pipe/filesize.pipe";
import { MatLabel } from "@angular/material/form-field";
import { MatIcon } from "@angular/material/icon";
import { MatTooltip } from "@angular/material/tooltip";
import { Subscription } from "rxjs";
import { FileUploadMonitorService } from "../../service/file-upload-monitor.service";

@Component({
  selector: "app-file-upload-snack-bar",
  templateUrl: "./file-upload-snack-bar.component.html",
  imports: [MatLabel, MatIcon, MatProgressBar, FilesizePipe, MatTooltip],
  styleUrls: ["./file-upload-snack-bar.component.scss"]
})
export class FileUploadSnackBarComponent implements OnDestroy {
  private changeDetectorRef = inject(ChangeDetectorRef);

  private filesChangedSubscription: Subscription;

  constructor(
    public snackBarRef: MatSnackBarRef<FileUploadSnackBarComponent>,
    @Inject(MAT_SNACK_BAR_DATA) public fileUploadMonitorService: FileUploadMonitorService
  ) {
    this.filesChangedSubscription = this.fileUploadMonitorService.uploadingFilesChanged.subscribe(() => {
      this.changeDetectorRef.markForCheck();
    });
  }

  ngOnDestroy() {
    this.filesChangedSubscription.unsubscribe();
  }

  getUploadingFiles() {
    return this.fileUploadMonitorService.getUploadingFiles();
  }

  cancelUpload(file: UploadingFile) {
    file.shouldCancel = true;
    console.log(file);
  }
}
