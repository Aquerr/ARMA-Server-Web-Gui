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
import { ModPresetEntry } from "../../../../model/mod.model";

@Component({
  selector: "app-mod-preset-import-dialog",
  imports: [
    MatButton,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    MatDialogTitle,
    ReactiveFormsModule
  ],
  templateUrl: "./mod-preset-import-dialog.component.html",
  styleUrl: "./mod-preset-import-dialog.component.scss"
})
export class ModPresetImportDialogComponent {
  public readonly modPresetName = signal<string | null>(null);
  public readonly modPresetEntries = signal<ModPresetEntry[]>([]);

  constructor(@Inject(MAT_DIALOG_DATA) dialogData: ModPresetImportDialogData) {
    this.modPresetName.set(dialogData.modPresetName);
    this.modPresetEntries.set(dialogData.modEntries);
  }
}

export interface ModPresetImportDialogData {
  modPresetName: string;
  modEntries: ModPresetEntry[];
}
