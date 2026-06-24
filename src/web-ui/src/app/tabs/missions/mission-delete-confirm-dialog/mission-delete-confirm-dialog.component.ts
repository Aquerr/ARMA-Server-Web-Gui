import { Component, ChangeDetectionStrategy } from "@angular/core";
import { MatDialogActions, MatDialogClose, MatDialogContent, MatDialogTitle } from "@angular/material/dialog";
import { MatButton } from "@angular/material/button";

@Component({
  selector: "app-mission-delete-confirm-dialog",
  templateUrl: "./mission-delete-confirm-dialog.component.html",
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatButton,
    MatDialogClose
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ["./mission-delete-confirm-dialog.component.scss"]
})
export class MissionDeleteConfirmDialogComponent {
}
