import { ChangeDetectorRef, Component, inject, OnDestroy, OnInit, signal } from "@angular/core";
import { FileDownloadMonitorService } from "@service/file-download-monitor.service";
import { Subscription } from "rxjs";
import { MatTooltip } from "@angular/material/tooltip";
import { MatProgressSpinner } from "@angular/material/progress-spinner";

@Component({
  selector: "app-aswg-download-snackbar-dongle",
  imports: [
    MatTooltip,
    MatProgressSpinner
  ],
  templateUrl: "./aswg-download-snackbar-dongle.component.html",
  styleUrl: "./aswg-download-snackbar-dongle.component.scss"
})
export class AswgDownloadSnackbarDongleComponent implements OnInit, OnDestroy {
  private readonly downloadMonitor = inject(FileDownloadMonitorService);
  private readonly changeDetectorRef = inject(ChangeDetectorRef);
  private subscription!: Subscription;

  isVisible = signal(false);
  count = signal(0);
  tooltip = signal("");

  ngOnInit() {
    this.subscription = this.downloadMonitor.downloadingFilesChanged
      .subscribe(() => {
        const files = this.downloadMonitor.getDownloadingFiles();
        this.count.set(files.length);
        this.tooltip.set(
          files.map((f) => `${f.fileName} (${f.progress}%)`).join("\n")
        );
        // show dongle only if files active AND snackbar is not open
        this.isVisible.set(
          files.length > 0 && !this.downloadMonitor.isSnackBarOpen()
        );
        this.changeDetectorRef.markForCheck();
      });
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  openSnackBar() {
    this.downloadMonitor.showDownloadProgressSnackBar();
    this.isVisible.set(false);
  }
}
