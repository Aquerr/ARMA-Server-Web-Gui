import { ChangeDetectionStrategy, Component } from "@angular/core";
import { MatDialogActions, MatDialogClose, MatDialogContent, MatDialogTitle } from "@angular/material/dialog";
import { MatButton } from "@angular/material/button";

@Component({
  selector: "app-difficulty-delete-confirm-dialog",
  templateUrl: "./difficulty-delete-confirm-dialog.component.html",
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatButton,
    MatDialogClose
  ],
  styleUrls: ["./difficulty-delete-confirm-dialog.component.scss"],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DifficultyDeleteConfirmDialogComponent {
}
