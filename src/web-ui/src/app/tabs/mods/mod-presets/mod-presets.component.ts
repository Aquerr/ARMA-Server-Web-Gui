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
import { ModPresetAddDialogComponent } from "./mod-preset-add-dialog/mod-preset-add-dialog.component";
import { Mod } from "../../../model/mod.model";
import { ModPresetParserService } from "./service/mod-preset-parser.service";
import { MatButton } from "@angular/material/button";
import { MatMenu, MatMenuItem, MatMenuTrigger } from "@angular/material/menu";
import { MatDivider } from "@angular/material/list";
import { ModPresetItemComponent } from "./mod-preset-item/mod-preset-item.component";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { DialogService } from "../../../service/dialog.service";
import {
  ModPresetImportDialogComponent,
  ModPresetImportDialogData
} from "./mod-preset-import-dialog/mod-preset-import-dialog.component";

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
    private dialogService: DialogService,
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

        this.maskService.hide();
        this.dialogService.open(ModPresetImportDialogComponent, (dialogResult: boolean) => {
          if (dialogResult) {
            const modPresetImportRequest = {
              name: modPreset.name ?? this.removeFileExtension(file.name),
              modParams: modPreset.entries.map(
                (entry) => ({ id: entry.id, title: entry.name }) as ModPresetModParam
              )
            } as ModPresetImportRequest;

            this.maskService.show();
            this.modsService.importPreset(modPresetImportRequest).subscribe(() => {
              this.maskService.hide();
              this.reloadModPresets();
              this.notificationService.successNotification(
                "Mod preset has been imported!",
                "Preset Imported!"
              );
            });
          }
        }, {
          modPresetName: modPreset.name ?? this.removeFileExtension(file.name),
          modEntries: modPreset.entries
        } as ModPresetImportDialogData);
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
    this.dialogService.open(ModPresetAddDialogComponent, (dialogResult: { create: boolean | undefined; modPresetName: string }) => {
      if (dialogResult.create) {
        this.modsService
          .savePreset({ name: dialogResult.modPresetName, modNames: [] })
          .subscribe(() => {
            this.reloadModPresets();
            this.notificationService.successNotification("Mod preset created!");
          });
      }
    }, {
      width: "250px",
      enterAnimationDuration: "200ms",
      exitAnimationDuration: "200ms"
    });
  }

  onPresetSaved(presetName: string) {
    this.showModalSavePreset(presetName);
  }

  private showModalSavePreset(presetName: string) {
    this.dialogService.openCommonConfirmationDialog({
      question: "<p>Enabled mods will be saved as selected preset.</p>"
        + "<p>Are you sure you want to save?</p>",
      confirmButtonLabel: "Save",
      cancelButtonLabel: "Cancel"
    }, (dialogResult: boolean) => {
      if (dialogResult) {
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
    this.dialogService.openCommonConfirmationDialog({
      question: "<p>Selected mod preset will be deleted.</p>"
        + "<p><b>No mods will be deleted in this process.</b></p>"
        + "<p>Are you sure you want to proceed?</p>",
      confirmButtonLabel: "Delete",
      cancelButtonLabel: "Cancel"
    }, (dialogResult: boolean) => {
      if (dialogResult) {
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
