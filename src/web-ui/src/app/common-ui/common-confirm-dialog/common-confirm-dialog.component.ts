import { Component, Inject, signal } from "@angular/core";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogTitle
} from "@angular/material/dialog";
import { MatButton } from "@angular/material/button";

@Component({
  selector: "app-common-confirm-dialog",
  templateUrl: "./common-confirm-dialog.component.html",
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatButton,
    MatDialogClose
  ],
  styleUrl: "./common-confirm-dialog.component.scss"
})
export class CommonConfirmDialogComponent {
  question: string = "";
  confirmButtonLabel = signal<string>("Yes");
  cancelButtonLabel = signal<string>("No");
  headerLabel = signal<string>("Confirmation");

  constructor(@Inject(MAT_DIALOG_DATA) dialogData: CommonConfirmDialogComponentDialogData) {
    this.question = dialogData.question;

    if (dialogData.headerLabel) this.headerLabel.set(dialogData.headerLabel);
    if (dialogData.confirmButtonLabel) this.confirmButtonLabel.set(dialogData.confirmButtonLabel);
    if (dialogData.cancelButtonLabel) this.cancelButtonLabel.set(dialogData.cancelButtonLabel);
  }
}

export interface CommonConfirmDialogComponentDialogData {
  question: string;
  headerLabel?: string;
  confirmButtonLabel?: string;
  cancelButtonLabel?: string;
}
