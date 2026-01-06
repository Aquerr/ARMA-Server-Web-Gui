import { Component, Inject } from "@angular/core";
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

  constructor(@Inject(MAT_DIALOG_DATA) dialogData: CommonConfirmDialogComponentDialogData) {
    this.question = dialogData.question;
  }
}

export interface CommonConfirmDialogComponentDialogData {
  question: string;
}
