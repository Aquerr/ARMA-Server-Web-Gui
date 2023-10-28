import {Component, ElementRef, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import {ModPresetImportRequest, ModPresetModParam, ServerModsService} from "../../../service/server-mods.service";
import {MaskService} from "../../../service/mask.service";
import {NotificationService} from "../../../service/notification.service";

@Component({
  selector: 'app-mod-presets',
  templateUrl: './mod-presets.component.html',
  styleUrls: ['./mod-presets.component.css']
})
export class ModPresetsComponent {

  @ViewChild("presetFileInput") fileInputComponent!: ElementRef;

  @Input() modPresets: string[] = [];
  @Output("modPresetSelected") modPresetSelected: EventEmitter<string> = new EventEmitter<string>();

  constructor(private modsService: ServerModsService,
              private maskService: MaskService,
              private notificationService: NotificationService) {}

  presetClicked(preset: string) {
    console.log("Selected preset: " + preset);
    // TODO: Show dialog "do you want to load selected preset? Any changes to mods list will be lost."

    this.modsService.selectPreset({name: preset}).subscribe(response => {
      this.modPresetSelected.emit(preset);
    });
  }

  modPresetImport(event: Event) {
    this.maskService.show();
    console.log(event);

    const target = (event.target as HTMLInputElement);

    if (!target.files)
      return;

    const file : File = target.files[0];
    if (file)
    {
      const reader = new FileReader();
      reader.onload = (data) => {
        this.processModPresetFile(reader.result);
        this.maskService.hide();
        this.notificationService.successNotification("Mod preset has been imported!", "Preset Imported!")
      }
      reader.readAsText(file);
    }
  }

  private processModPresetFile(result: string | ArrayBuffer | null) {
    if (!(typeof result === "string")) {
      console.error("Could not process file due to bad type...");
      return;
    }

    const html = document.createElement("html");
    html.innerHTML = result;
    const table = html.getElementsByClassName("mod-list")[0].firstElementChild as HTMLTableElement;

    const mods: ModPresetModParam[] = [];
    for (let i = 0; i < table.rows.length; i++) {
      const row = table.rows[i];
      const modId = this.getModIdFromRow(row);
      const modTitle = this.getModTitleFromRow(row);
      if (modId != null && modTitle != null) {
        mods.push({id: modId, title: modTitle});
      }
    }
    console.log(mods);

    // TODO: Get name from modal...
    this.modsService.importPreset({name:"custom", modParams: mods} as ModPresetImportRequest).subscribe(response => {
      console.log("Done!");
    });
  }

  private getModTitleFromRow(row: HTMLTableRowElement): string | null {
    for (let i = 0; i < row.cells.length; i++) {
      const cell = row.cells[i];
      if (cell.getAttribute("data-type") == "DisplayName") {
        const modTitle = cell.innerText;
        return modTitle;
      }
    }
    return null;
  }

  private getModIdFromRow(row: HTMLTableRowElement): number | null {
    for (let i = 0; i < row.cells.length; i++) {
      const cell = row.cells[i];
      const linkElement = cell.firstElementChild;
      if (linkElement != null && linkElement.getAttribute("data-type") == "Link") {
        const href = linkElement.getAttribute("href");
        if (href == null)
          return null;
        const id = href.substring(href.lastIndexOf("?id=") + 4, href.length);
        return Number(id);
      }
    }
    return null;
  }

  newPresetClick() {
    console.log("New Preset click!");
  }

  presetImportClick() {
    console.log(this.fileInputComponent);
    this.fileInputComponent.nativeElement.click();
  }
}
