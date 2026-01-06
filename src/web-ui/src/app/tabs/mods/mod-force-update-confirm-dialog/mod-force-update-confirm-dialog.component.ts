import { Component } from "@angular/core";
import { MatDialogActions, MatDialogClose, MatDialogContent, MatDialogTitle } from "@angular/material/dialog";
import { MatButton } from "@angular/material/button";

@Component({
  selector: "app-mod-force-update-confirm-dialog",
  templateUrl: "./mod-force-update-confirm-dialog.component.html",
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatButton,
    MatDialogClose
  ],
  styleUrls: ["./mod-force-update-confirm-dialog.component.scss"]
})
export class ModForceUpdateConfirmDialogComponent {}
