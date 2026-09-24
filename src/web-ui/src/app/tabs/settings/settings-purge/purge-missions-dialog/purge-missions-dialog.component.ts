import { Component, inject, model } from "@angular/core";
import { MatButton } from "@angular/material/button";
import {
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import { MatCheckbox } from "@angular/material/checkbox";
import { FormsModule } from "@angular/forms";

@Component({
  selector: "app-purge-missions-dialog",
  imports: [
    MatButton,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    MatDialogTitle,
    MatCheckbox,
    FormsModule
  ],
  templateUrl: "./purge-missions-dialog.component.html",
  styleUrl: "./purge-missions-dialog.component.scss"
})
export class PurgeMissionsDialogComponent {
  private readonly dialogRef: MatDialogRef<PurgeMissionsDialogComponent>
    = inject<MatDialogRef<PurgeMissionsDialogComponent>>(MatDialogRef<PurgeMissionsDialogComponent>);

  public deleteFiles = model<boolean>();

  public purge(): void {
    this.dialogRef.close({
      deleteFiles: this.deleteFiles()
    });
  }

  public close(): void {
    this.dialogRef.close();
  }
}
