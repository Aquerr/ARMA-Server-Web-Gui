import { Component } from "@angular/core";
import { MatDialogActions, MatDialogClose, MatDialogContent, MatDialogTitle } from "@angular/material/dialog";
import { MatButton } from "@angular/material/button";

@Component({
  selector: "app-mod-preset-save-dialog",
  templateUrl: "./mod-preset-save-dialog.component.html",
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatButton,
    MatDialogClose
  ],
  styleUrls: ["./mod-preset-save-dialog.component.scss"]
})
export class ModPresetSaveDialogComponent {}
