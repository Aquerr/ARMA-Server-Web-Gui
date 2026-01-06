import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { WorkshopMod } from "../../../model/workshop.model";
import { WorkshopService } from "../../../service/workshop.service";
import { ServerModsService } from "../../../service/server-mods.service";
import { LoadingSpinnerMaskService } from "../../../service/loading-spinner-mask.service";
import { MatButton } from "@angular/material/button";
import { AswgSpinnerComponent } from "../../../aswg-spinner/aswg-spinner.component";

@Component({
  selector: "app-workshop-item",
  templateUrl: "./workshop-item.component.html",
  imports: [
    MatButton,
    AswgSpinnerComponent
  ],
  styleUrls: ["./workshop-item.component.scss"]
})
export class WorkshopItemComponent implements OnInit {
  @Input() workshopMod!: WorkshopMod;
  @Input() canInstall: boolean = false;
  @Output() modInstallDelete = new EventEmitter<void>();

  spinnerVisible: boolean = false;
  spinnerColor: string = "";

  constructor(
    private workshopService: WorkshopService,
    private serverModsService: ServerModsService,
    private maskService: LoadingSpinnerMaskService
  ) {}

  ngOnInit(): void {
    if (this.workshopMod.isBeingInstalled) {
      this.showSpinner(true);
    }
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

  installMod(mod: WorkshopMod) {
    mod.isBeingInstalled = true;
    this.showSpinner(true);
    this.workshopService.installMod(mod.fileId, mod.title).subscribe(() => {
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

  showSpinner(value: boolean) {
    setTimeout(() => {
      if (value) {
        this.maskService.show();
      } else {
        this.maskService.hide();
      }
      this.spinnerVisible = value;
    }, 2000);
  }
}
