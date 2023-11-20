import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-mod-preset-item',
  templateUrl: './mod-preset-item.component.html',
  styleUrls: ['./mod-preset-item.component.css']
})
export class ModPresetItemComponent {

  @Input("presetName") presetName: string = "";

  @Output("presetSelected") presetSelectedEmitter = new EventEmitter<string>();
  @Output("presetDeleted") presetDeletedEmitter = new EventEmitter<string>();
  @Output("presetSaved") presetSavedEmitter = new EventEmitter<string>();

  deletePreset($event: MouseEvent) {
    console.log("Deleting preset: " + this.presetName);
    $event.stopPropagation();
    this.presetDeletedEmitter.emit(this.presetName);
  }

  savePreset($event: MouseEvent) {
    console.log("Saving preset: " + this.presetName);
    $event.stopPropagation();
    this.presetSavedEmitter.emit(this.presetName);
  }

  presetSelected() {
    console.log("Selected preset: " + this.presetName);
    this.presetSelectedEmitter.emit(this.presetName);
  }
}
