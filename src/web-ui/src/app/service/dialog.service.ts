import { Injectable } from '@angular/core';
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {ComponentType} from "@angular/cdk/overlay";
import {
  CommonConfirmDialogComponent,
  CommonConfirmDialogComponentDialogData
} from "../common-ui/common-confirm-dialog/common-confirm-dialog.component";

@Injectable({
  providedIn: 'root'
})
export class DialogService {

  constructor(private matDialog: MatDialog) { }

  openCommonConfirmationDialog(data: CommonConfirmDialogComponentDialogData, closeCallBack: (dialogResult: boolean) => any) : void {
    this.matDialog.open(CommonConfirmDialogComponent, this.prepareDialogConfig(undefined, data)).afterClosed().subscribe(closeCallBack);
  }

  open<T>(dialogComponent: ComponentType<T>,
          closeCallback: (dialogResult: any) => any,
          data: any,
          config?: MatDialogConfig) : void {

    const dialogConfig= this.prepareDialogConfig(config, data);

    const subscription = this.matDialog.open(dialogComponent, dialogConfig).afterClosed().subscribe({
      next: data => {
        closeCallback(data);
        subscription.unsubscribe();
      }
    });
  }

  private prepareDialogConfig(initialConfig?: MatDialogConfig, data?: any) {
    if (initialConfig) {
      return {
        ...initialConfig,
        data: data
      }
    } else {
      return {
        width: '350px',
        enterAnimationDuration: '200ms',
        exitAnimationDuration: '200ms',
        data: data
      }
    }
  }
}
