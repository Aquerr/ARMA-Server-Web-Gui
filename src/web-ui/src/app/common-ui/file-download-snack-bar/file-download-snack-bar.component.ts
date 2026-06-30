import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Inject, inject, OnDestroy } from "@angular/core";
import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from "@angular/material/snack-bar";
import { MatProgressBar } from "@angular/material/progress-bar";
import { FilesizePipe } from "@app/util/pipe/filesize.pipe";
import { MatIcon } from "@angular/material/icon";
import { MatTooltip } from "@angular/material/tooltip";
import { Subscription } from "rxjs";
import { DownloadingFile, FileDownloadMonitorService } from "@service/file-download-monitor.service";

@Component({
  selector: "app-file-download-snack-bar",
  templateUrl: "./file-download-snack-bar.component.html",
  imports: [MatIcon, MatProgressBar, FilesizePipe, MatTooltip],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ["./file-download-snack-bar.component.scss"]
})
export class FileDownloadSnackBarComponent implements OnDestroy {
  private readonly changeDetectorRef = inject(ChangeDetectorRef);

  private filesChangedSubscription: Subscription;

  constructor(
    public snackBarRef: MatSnackBarRef<FileDownloadSnackBarComponent>,
    @Inject(MAT_SNACK_BAR_DATA) public fileDownloadMonitorService: FileDownloadMonitorService
  ) {
    this.filesChangedSubscription = this.fileDownloadMonitorService.downloadingFilesChanged.subscribe(() => {
      this.changeDetectorRef.markForCheck();
    });
  }

  ngOnDestroy() {
    this.filesChangedSubscription.unsubscribe();
  }

  getDownloadingFiles() {
    return this.fileDownloadMonitorService.getDownloadingFiles();
  }

  cancelDownload(file: DownloadingFile) {
    file.shouldCancel = true;
    console.log(file);
  }
}
