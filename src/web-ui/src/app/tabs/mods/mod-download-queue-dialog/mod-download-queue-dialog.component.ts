import { Component, inject, OnInit } from "@angular/core";
import { LoadingSpinnerMaskService } from "../../../service/loading-spinner-mask.service";
import { WorkshopService } from "../../../service/workshop.service";
import { DownloadingMod } from "../../../model/workshop.model";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatDialogClose, MatDialogContent, MatDialogTitle } from "@angular/material/dialog";
import { MatIcon } from "@angular/material/icon";
import { MatIconButton } from "@angular/material/button";

@Component({
  selector: "app-mod-download-queue-dialog",
  imports: [
    FormsModule,
    MatDialogContent,
    MatDialogTitle,
    ReactiveFormsModule,
    MatIcon,
    MatIconButton,
    MatDialogClose
  ],
  templateUrl: "./mod-download-queue-dialog.component.html",
  styleUrl: "./mod-download-queue-dialog.component.scss"
})
export class ModDownloadQueueDialogComponent implements OnInit {
  private readonly loadingSpinnerMaskService: LoadingSpinnerMaskService
    = inject(LoadingSpinnerMaskService);

  private readonly workshopService: WorkshopService = inject(WorkshopService);

  modDownloadQueue: DownloadingMod[] = [];

  ngOnInit() {
    this.loadingSpinnerMaskService.show();
    this.workshopService.getModDownloadQueue().subscribe((response) => {
      this.modDownloadQueue = response.mods;
      this.loadingSpinnerMaskService.hide();
    });
  }
}
