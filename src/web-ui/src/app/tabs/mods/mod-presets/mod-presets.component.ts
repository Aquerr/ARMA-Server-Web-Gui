import {
  ChangeDetectionStrategy, ChangeDetectorRef,
  Component,
  ElementRef,
  EventEmitter,
  input,
  Output,
  signal,
  ViewChild
} from "@angular/core";
import {
  ModPresetImportRequest,
  ModPresetModParam,
  ModPresetSaveRequest,
  ServerModsService
} from "../../../service/server-mods.service";
import { LoadingSpinnerMaskService } from "../../../service/loading-spinner-mask.service";
import { NotificationService } from "../../../service/notification.service";
import { MatDialog } from "@angular/material/dialog";
import { ModPresetAddDialogComponent } from "./mod-preset-add-dialog/mod-preset-add-dialog.component";
import { ModPresetSaveDialogComponent } from "./mod-preset-save-dialog/mod-preset-save-dialog.component";
import { Mod } from "../../../model/mod.model";
import { ModPresetDeleteDialogComponent } from "./mod-preset-delete-dialog/mod-preset-delete-dialog.component";
import { ModPresetParserService } from "./service/mod-preset-parser.service";
import { MatButton } from "@angular/material/button";
import { MatMenu, MatMenuItem, MatMenuTrigger } from "@angular/material/menu";
import { MatDivider } from "@angular/material/list";
import { ModPresetItemComponent } from "./mod-preset-item/mod-preset-item.component";
import { MatPaginator, PageEvent } from "@angular/material/paginator";

@Component({
  selector: "app-mod-presets",
  templateUrl: "./mod-presets.component.html",
  imports: [
    MatButton,
    MatMenu,
    MatDivider,
    MatMenuItem,
    ModPresetItemComponent,
    MatMenuTrigger,
    MatPaginator
  ],
  styleUrls: ["./mod-presets.component.scss"],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ModPresetsComponent {
  @ViewChild("presetFileInput") fileInputComponent!: ElementRef<HTMLInputElement>;

  enabledMods = input.required<Mod[]>();
  modPresets: string[] = [];

  @Output() modPresetSelected: EventEmitter<string> = new EventEmitter<string>();

  // Paginator
  totalPresets = signal(0);
  presetsToShow: string[] = [];
  pageIndex: number = 0;

  constructor(
    private modsService: ServerModsService,
    private maskService: LoadingSpinnerMaskService,
    private notificationService: NotificationService,
    private matDialog: MatDialog,
    private modPresetParserService: ModPresetParserService,
    private changeDetectorRef: ChangeDetectorRef
  ) {
    this.reloadModPresets();
  }

  modPresetImport(event: Event) {
    this.maskService.show();

    const target = event.target as HTMLInputElement;

    if (!target.files) return;

    const file: File = target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        const modPreset = this.modPresetParserService.processModPresetFile(reader.result);

        if (modPreset == null) {
          this.notificationService.errorNotification(
            "Could not parse mod preset! Check console for more info.",
            "Preset parse error"
          );
          return;
        }

        const modPresetImportRequest = {
          name: modPreset.name ?? this.removeFileExtension(file.name),
          modParams: modPreset.entries.map(
            (entry) => ({ id: entry.id, title: entry.name }) as ModPresetModParam
          )
        } as ModPresetImportRequest;

        this.modsService.importPreset(modPresetImportRequest).subscribe(() => {
          this.maskService.hide();
          this.reloadModPresets();
          this.notificationService.successNotification(
            "Mod preset has been imported!",
            "Preset Imported!"
          );
        });
      };
      reader.readAsText(file);
    }
  }

  private removeFileExtension(fileName: string): string {
    return fileName.replace(/\.[^/.]+$/, "");
  }

  private reloadModPresets() {
    this.maskService.show();
    this.modsService.getModPresetsNames().subscribe((response) => {
      this.modPresets = response.presets;
      this.totalPresets.set(this.modPresets.length);
      this.showPresetsPage(0, 10);
      this.maskService.hide();
    });
  }

  newPresetClick() {
    this.showModalNewPreset();
  }

  presetImportClick() {
    this.fileInputComponent.nativeElement.click();
  }

  onPresetSelect(presetName: string) {
    this.maskService.show();
    this.modsService.selectPreset({ name: presetName }).subscribe(() => {
      this.modPresetSelected.emit(presetName);
      this.maskService.hide();
      this.notificationService.successNotification("Mod preset loaded!");
    });
  }

  onPresetDelete(presetName: string) {
    this.showModalDeletePreset(presetName);
  }

  private removePresetFromList(list: string[], presetName: string) {
    list.forEach((value, index) => {
      if (value == presetName) list.splice(index, 1);
    });
  }

  private showModalNewPreset() {
    const dialogRef = this.matDialog.open(ModPresetAddDialogComponent, {
      width: "250px",
      enterAnimationDuration: "200ms",
      exitAnimationDuration: "200ms"
    });

    dialogRef.afterClosed().subscribe((result: { create: boolean | undefined; modPresetName: string }) => {
      if (result.create) {
        this.modsService
          .savePreset({ name: result.modPresetName, modNames: [] })
          .subscribe(() => {
            this.reloadModPresets();
            this.notificationService.successNotification("Mod preset created!");
          });
      }
    });
  }

  onPresetSaved(presetName: string) {
    this.showModalSavePreset(presetName);
  }

  private showModalSavePreset(presetName: string) {
    const dialogRef = this.matDialog.open(ModPresetSaveDialogComponent, {
      width: "250px",
      enterAnimationDuration: "200ms",
      exitAnimationDuration: "200ms"
    });

    dialogRef.afterClosed().subscribe((result: { create: boolean; modPresetName: string }) => {
      if (result) {
        this.modsService
          .savePreset({
            name: presetName,
            modNames: this.enabledMods().map((mod) => mod.name)
          } as ModPresetSaveRequest)
          .subscribe(() => {
            this.reloadModPresets();
            this.notificationService.successNotification("Mod preset saved!");
          });
      }
    });
  }

  private showModalDeletePreset(presetName: string) {
    const dialogRef = this.matDialog.open(ModPresetDeleteDialogComponent, {
      width: "350px",
      enterAnimationDuration: "200ms",
      exitAnimationDuration: "200ms"
    });

    dialogRef.afterClosed().subscribe((result: boolean) => {
      if (result) {
        this.maskService.show();
        this.modsService.deletePreset(presetName).subscribe(() => {
          this.removePresetFromList(this.modPresets, presetName);
          this.maskService.hide();
          this.notificationService.successNotification("Mod preset deleted!");
        });
      }
    });
  }

  public changePage(event: PageEvent): void {
    this.showPresetsPage(event.pageIndex, event.pageSize);
  }

  private showPresetsPage(pageIndex: number, pageSize: number) {
    const startIndex = pageIndex * pageSize;
    const endIndex = pageIndex * pageSize + pageSize;

    this.presetsToShow = this.modPresets.slice(startIndex, endIndex);
    this.pageIndex = pageIndex;
    this.changeDetectorRef.markForCheck();
  }

  onPaginationClick($event: PointerEvent) {
    $event.stopPropagation();
    $event.preventDefault();
  }
}
