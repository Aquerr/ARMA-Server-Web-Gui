import {Component} from '@angular/core';
import {MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-mod-preset-add-dialog',
  templateUrl: './mod-preset-add-dialog.component.html',
  styleUrls: ['./mod-preset-add-dialog.component.css']
})
export class ModPresetAddDialogComponent {

  modPresetName: string = '';

  constructor(private dialogRef: MatDialogRef<ModPresetAddDialogComponent>) {}


  onEnterClick($event: KeyboardEvent) {
    if ($event.code === 'Enter') {
      this.dialogRef.close(this.prepareDialogResult());
    }
  }

  prepareDialogResult() {
    return {'create':true, 'modPresetName': this.modPresetName};
  }
}
