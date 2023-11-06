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

  deletePreset($event: MouseEvent) {
    console.log("Deleting preset: " + this.presetName);
    this.presetDeletedEmitter.emit(this.presetName);
  }

  savePreset($event: MouseEvent) {
    console.log("Saving preset: " + this.presetName);
    $event.stopPropagation();
  }

  presetSelected() {
    console.log("Selected preset: " + this.presetName);
    // TODO: Show dialog "do you want to load selected preset? Any changes to mods list will be lost."

    this.presetSelectedEmitter.emit(this.presetName);
  }
}
