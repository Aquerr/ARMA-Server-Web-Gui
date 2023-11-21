import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {WorkshopMod} from '../../../model/workshop.model';
import {WorkshopService} from "../../../service/workshop.service";
import {ServerModsService} from "../../../service/server-mods.service";
import {MaskService} from "../../../service/mask.service";
import {NgxSpinnerService} from "ngx-spinner";

@Component({
  selector: 'app-workshop-item',
  templateUrl: './workshop-item.component.html',
  styleUrls: ['./workshop-item.component.css']
})
export class WorkshopItemComponent implements OnInit {
  @Input() workshopMod!: WorkshopMod;
  @Input() canInstall: boolean = false;
  @Output() onModInstallDelete = new EventEmitter<any>();

  spinnerVisible: boolean = false;
  spinnerColor: string = "";

  constructor(private workshopService: WorkshopService,
              private serverModsService: ServerModsService,
              private maskService: MaskService,
              private ngxSpinner: NgxSpinnerService) {
  }

  ngOnInit(): void {
    if (this.workshopMod.isBeingInstalled) {
      this.showSpinner(true);
    }
    this.spinnerColor = document.documentElement.style.getPropertyValue('--aswg-primary-color');
  }

  prepareModDescription(description: string | undefined) {
    let result = description;
    if (description) {
      result = this.removeFormattingCharacters(description);
      result = this.shorten(result, 400);

      if(description.length > result.length) {
        result += "...";
      }
    }

    return result;
  }

  private removeFormattingCharacters(text: string): string {
    const regex = /\[h1\]|\[\/h1\]|\[h2\]|\[\/h2\]|\[b\]|\[\/b\]|\[u\]|\[\/u\]|\[i\]|\[\/i\]|\[url.*\]|\[\/url\]|\[list\]|\[\/list\]|\[olist\]|\[\/olist\]|\[code\]|\[\/code\\]|\[table\]|\[\/table\]|\[\*\]/g;
    return text.replace(regex, "");
  }

  private shorten(text: string, maxLen: number, separator = ' ') {
    if (text.length <= maxLen) return text;
    return text.substring(0, text.lastIndexOf(separator, maxLen));
  }

  installMod(mod: WorkshopMod) {
    mod.isBeingInstalled = true;
    this.showSpinner(true);
    this.workshopService.installMod(mod.fileId, mod.title).subscribe(response => {
      this.onModInstallDelete.emit();
    });
  }

  deleteMod(workshopMod: WorkshopMod) {
    this.maskService.show();
    this.serverModsService.deleteMod(workshopMod.title).subscribe(response => {
      this.maskService.hide();
      this.onModInstallDelete.emit();
    });
  }

  showSpinner(value: boolean) {
    setTimeout(() => {
      console.log("Setting spinnerVisible to = " + value);
      if (value) {
        this.ngxSpinner.show(this.workshopMod.title);
      } else {
        this.ngxSpinner.hide(this.workshopMod.title);
      }
      this.spinnerVisible = value;
    }, 2000);
  }
}
