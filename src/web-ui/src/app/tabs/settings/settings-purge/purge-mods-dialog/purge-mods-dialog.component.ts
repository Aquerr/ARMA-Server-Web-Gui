import { Component, inject, model } from "@angular/core";
import { MatButton } from "@angular/material/button";
import { MatCheckbox } from "@angular/material/checkbox";
import {
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import { FormsModule } from "@angular/forms";

@Component({
  selector: "app-purge-mods-dialog",
  imports: [
    MatButton,
    MatCheckbox,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    MatDialogTitle,
    FormsModule
  ],
  templateUrl: "./purge-mods-dialog.component.html",
  styleUrl: "./purge-mods-dialog.component.scss"
})
export class PurgeModsDialogComponent {
  private readonly dialogRef: MatDialogRef<PurgeModsDialogComponent>
    = inject<MatDialogRef<PurgeModsDialogComponent>>(MatDialogRef<PurgeModsDialogComponent>);

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
