import { Component } from "@angular/core";
import { MatDialogActions, MatDialogClose, MatDialogContent, MatDialogTitle } from "@angular/material/dialog";
import { MatButton } from "@angular/material/button";

@Component({
  selector: "app-mod-delete-confirm-dialog",
  templateUrl: "./mod-delete-confirm-dialog.component.html",
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatButton,
    MatDialogClose
  ],
  styleUrls: ["./mod-delete-confirm-dialog.component.scss"]
})
export class ModDeleteConfirmDialogComponent {
}
