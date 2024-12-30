import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";

@Component({
    selector: 'app-common-confirm-dialog',
    templateUrl: './common-confirm-dialog.component.html',
    styleUrl: './common-confirm-dialog.component.scss',
    standalone: false
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
