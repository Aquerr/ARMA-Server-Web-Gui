import { Injectable } from "@angular/core";
import { MatDialog, MatDialogConfig } from "@angular/material/dialog";
import { ComponentType } from "@angular/cdk/overlay";
import {
  CommonConfirmDialogComponent,
  CommonConfirmDialogComponentDialogData
} from "../common-ui/common-confirm-dialog/common-confirm-dialog.component";
import { take } from "rxjs";

@Injectable({
  providedIn: "root"
})
export class DialogService {
  constructor(private matDialog: MatDialog) {}

  openCommonConfirmationDialog(
    data: CommonConfirmDialogComponentDialogData,
    closeCallBack: (dialogResult: boolean) => void
  ): void {
    this.matDialog
      .open(CommonConfirmDialogComponent, this.prepareDialogConfig(undefined, data))
      .afterClosed()
      .subscribe(closeCallBack);
  }

  open<T, R>(
    dialogComponent: ComponentType<T>,
    closeCallback: (dialogResult: R) => void,
    data?: unknown,
    config?: MatDialogConfig
  ): void {
    const dialogConfig = this.prepareDialogConfig(config, data);

    this.matDialog
      .open(dialogComponent, dialogConfig)
      .afterClosed()
      .pipe(take(1))
      .subscribe({
        next: (dialogResult: R) => {
          closeCallback(dialogResult);
        }
      });
  }

  private prepareDialogConfig(initialConfig?: MatDialogConfig, data?: unknown) {
    if (initialConfig) {
      return {
        ...initialConfig,
        data: data
      };
    } else {
      return {
        maxWidth: "450px",
        enterAnimationDuration: "200ms",
        exitAnimationDuration: "200ms",
        data: data
      };
    }
  }
}
