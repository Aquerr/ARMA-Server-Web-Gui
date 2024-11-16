import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-mod-preset-item',
  templateUrl: './mod-preset-item.component.html',
  styleUrls: ['./mod-preset-item.component.scss']
})
export class ModPresetItemComponent {

  @Input("presetName") presetName: string = "";

  @Output("presetSelected") presetSelectedEmitter = new EventEmitter<string>();
  @Output("presetDeleted") presetDeletedEmitter = new EventEmitter<string>();
  @Output("presetSaved") presetSavedEmitter = new EventEmitter<string>();

  deletePreset($event: MouseEvent) {
    $event.stopPropagation();
    this.presetDeletedEmitter.emit(this.presetName);
  }

  savePreset($event: MouseEvent) {
    $event.stopPropagation();
    this.presetSavedEmitter.emit(this.presetName);
  }

  presetSelected() {
    this.presetSelectedEmitter.emit(this.presetName);
  }
}
