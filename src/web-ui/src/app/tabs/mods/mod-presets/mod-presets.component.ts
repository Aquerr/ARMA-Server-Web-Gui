import {Component, ElementRef, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import {
  ModPresetImportRequest,
  ModPresetModParam,
  ModPresetSaveRequest,
  ServerModsService
} from "../../../service/server-mods.service";
import {MaskService} from "../../../service/mask.service";
import {NotificationService} from "../../../service/notification.service";
import {MatDialog} from "@angular/material/dialog";
import {ModPresetAddDialogComponent} from "./mod-preset-add-dialog/mod-preset-add-dialog.component";
import {ModPresetSaveDialogComponent} from "./mod-preset-save-dialog/mod-preset-save-dialog.component";
import {Mod} from "../../../model/mod.model";

@Component({
  selector: 'app-mod-presets',
  templateUrl: './mod-presets.component.html',
  styleUrls: ['./mod-presets.component.css']
})
export class ModPresetsComponent {

  @ViewChild("presetFileInput") fileInputComponent!: ElementRef;

  @Input("mods") enabledMods: Mod[] = [];
  modPresets: string[] = [];

  @Output("modPresetSelected") modPresetSelected: EventEmitter<string> = new EventEmitter<string>();

  constructor(private modsService: ServerModsService,
              private maskService: MaskService,
              private notificationService: NotificationService,
              private matDialog: MatDialog) {
    this.reloadModPresets()
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

  private reloadModPresets() {
    this.maskService.show();
    this.modsService.getModPresetsNames().subscribe(response => {
      this.modPresets = response.presets;
      this.maskService.hide();
    });
  }

  private processModPresetFile(result: string | ArrayBuffer | null) {
    if (typeof result !== "string") {
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

    const presetName = html.querySelector('meta[name="arma:PresetName"]')?.getAttribute('content') ?? "custom";

    this.modsService.importPreset({name:presetName, modParams: mods} as ModPresetImportRequest).subscribe(response => {
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
    this.showModalNewPreset();
  }

  presetImportClick() {
    console.log(this.fileInputComponent);
    this.fileInputComponent.nativeElement.click();
  }

  onPresetSelect(presetName: string) {
    this.maskService.show();
    this.modsService.selectPreset({name: presetName}).subscribe(response => {
      this.modPresetSelected.emit(presetName);
      this.maskService.hide();
    });
  }

  onPresetDelete(presetName: string) {
    this.maskService.show();
    this.modsService.deletePreset(presetName).subscribe(response => {
      this.removePresetFromList(this.modPresets, presetName);
      this.maskService.hide();
    });
  }

  private removePresetFromList(list: string[], presetName: string) {
    list.forEach((value, index) => {
      if (value == presetName) list.splice(index, 1);
    });
  }

  private showModalNewPreset() {
    const dialogRef = this.matDialog.open(ModPresetAddDialogComponent, {
      width: '250px',
      enterAnimationDuration: '200ms',
      exitAnimationDuration: '200ms'
    });

    dialogRef.afterClosed().subscribe((result: {create: boolean, modPresetName: string}) => {
      console.log(result);
      if (result.create) {
        this.modsService.savePreset({name: result.modPresetName, modNames: []}).subscribe(response => {
          this.reloadModPresets();
        });
      }
    });
  }

  onPresetSaved(presetName: string) {
    console.log(`Saving preset ${presetName} with mods [${this.enabledMods}]`);
    this.showModalSavePreset(presetName);
  }

  private showModalSavePreset(presetName: string) {
    const dialogRef = this.matDialog.open(ModPresetSaveDialogComponent, {
      width: '250px',
      enterAnimationDuration: '200ms',
      exitAnimationDuration: '200ms'
    });

    dialogRef.afterClosed().subscribe((result: {create: boolean, modPresetName: string}) => {
      console.log(result);
      if (result) {
        this.modsService.savePreset({name: presetName, modNames: this.enabledMods.map(mod => mod.name)} as ModPresetSaveRequest).subscribe(response => {
          this.reloadModPresets();
        });
      }
    });
  }
}
