import { Component, Inject, signal } from "@angular/core";
import { MatButton } from "@angular/material/button";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogTitle
} from "@angular/material/dialog";
import { ReactiveFormsModule } from "@angular/forms";
import { ModStatus } from "../../../../model/mod.model";
import { MatIcon } from "@angular/material/icon";
import { MatTooltip } from "@angular/material/tooltip";
import { NgClass } from "@angular/common";
import { PresetModStatusToIconPipe } from "../preset-mod-status-to-icon.pipe";

@Component({
  selector: "app-mod-preset-import-dialog",
  imports: [
    MatButton,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    MatDialogTitle,
    ReactiveFormsModule,
    MatIcon,
    MatTooltip,
    NgClass,
    PresetModStatusToIconPipe
  ],
  templateUrl: "./mod-preset-import-confirmation-dialog.component.html",
  styleUrl: "./mod-preset-import-confirmation-dialog.component.scss"
})
export class ModPresetImportConfirmationDialogComponent {
  public readonly modPresetName = signal<string | null>(null);
  public readonly modPresetEntries = signal<ModToImport[]>([]);

  constructor(@Inject(MAT_DIALOG_DATA) dialogData: ModPresetImportDialogData) {
    this.modPresetName.set(dialogData.modPresetName);
    this.modPresetEntries.set(dialogData.modEntries);
  }
}

export interface ModPresetImportDialogData {
  modPresetName: string;
  modEntries: ModToImport[];
}

export interface ModToImport {
  id: number;
  name: string;
  status: ModStatus;
}
