import { Component } from "@angular/core";
import { MatDialogActions, MatDialogClose, MatDialogContent, MatDialogTitle } from "@angular/material/dialog";
import { MatButton } from "@angular/material/button";

@Component({
  selector: "app-mod-preset-delete-dialog",
  templateUrl: "./mod-preset-delete-dialog.component.html",
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatButton,
    MatDialogClose
  ],
  styleUrls: ["./mod-preset-delete-dialog.component.scss"]
})
export class ModPresetDeleteDialogComponent {}
