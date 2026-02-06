import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { WorkshopMod } from "../../../model/workshop.model";
import { ModDependencyStatus, WorkshopService } from "../../../service/workshop.service";
import { ServerModsService } from "../../../service/server-mods.service";
import { LoadingSpinnerMaskService } from "../../../service/loading-spinner-mask.service";
import { MatButton } from "@angular/material/button";
import { DialogService } from "../../../service/dialog.service";
import {
  WorkshopInstallDependenciesDialogComponent
} from "../workshop-install-dependencies-dialog/workshop-install-dependencies-dialog.component";

@Component({
  selector: "app-workshop-item",
  templateUrl: "./workshop-item.component.html",
  imports: [
    MatButton
  ],
  styleUrls: ["./workshop-item.component.scss"]
})
export class WorkshopItemComponent implements OnInit {
  @Input() workshopMod!: WorkshopMod;
  @Input() canInstall: boolean = false;
  @Output() modInstallDelete = new EventEmitter<void>();

  spinnerColor: string = "";

  constructor(
    private workshopService: WorkshopService,
    private serverModsService: ServerModsService,
    private maskService: LoadingSpinnerMaskService,
    private dialogService: DialogService
  ) {}

  ngOnInit(): void {
    this.spinnerColor = document.documentElement.style.getPropertyValue("--aswg-primary-color");
  }

  prepareModDescription(description: string | undefined) {
    let result = description;
    if (description) {
      result = this.removeFormattingCharacters(description);
      result = this.shorten(result, 400);

      if (description.length > result.length) {
        result += "...";
      }
    }

    return result;
  }

  private removeFormattingCharacters(text: string): string {
    const regex
      = /\[h1\]|\[\/h1\]|\[h2\]|\[\/h2\]|\[b\]|\[\/b\]|\[u\]|\[\/u\]|\[i\]|\[\/i\]|\[url.*\]|\[\/url\]|\[list\]|\[\/list\]|\[olist\]|\[\/olist\]|\[code\]|\[\/code\\]|\[table\]|\[\/table\]|\[\*\]/g;
    return text.replace(regex, "");
  }

  private shorten(text: string, maxLen: number, separator = " ") {
    if (text.length <= maxLen) return text;
    return text.substring(0, text.lastIndexOf(separator, maxLen));
  }

  installMod(selectedWorkshopMod: WorkshopMod) {
    this.maskService.show();
    this.workshopService.getModDependencies(selectedWorkshopMod.fileId).subscribe((response) => {
      this.maskService.hide();

      const missingDependencies = response.dependencies.filter((dependency) => dependency.status === ModDependencyStatus.NOT_INSTALLED);
      if (missingDependencies.length > 0) {
        this.dialogService.open(WorkshopInstallDependenciesDialogComponent, (dialogResult: string) => {
          if (!dialogResult) {
            return;
          }

          const installWithDependencies = dialogResult == "install-with-dependencies";
          this.doInstallMod(selectedWorkshopMod, installWithDependencies);
        }, missingDependencies, {
          width: "600px"
        });
      } else {
        this.doInstallMod(selectedWorkshopMod, false);
      }
    });
  }

  doInstallMod(selectedWorkshopMod: WorkshopMod, installDependencies: boolean): void {
    selectedWorkshopMod.isBeingInstalled = true;
    this.workshopService.installMod(selectedWorkshopMod.fileId, selectedWorkshopMod.title, installDependencies).subscribe(() => {
      this.modInstallDelete.emit();
    });
  }

  deleteMod(workshopMod: WorkshopMod) {
    this.maskService.show();
    this.serverModsService.deleteMod(workshopMod.title).subscribe(() => {
      this.maskService.hide();
      this.modInstallDelete.emit();
    });
  }
}
