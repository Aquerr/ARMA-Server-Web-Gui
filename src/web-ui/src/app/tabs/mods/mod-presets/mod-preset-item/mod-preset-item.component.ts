import { Component, EventEmitter, Input, Output } from "@angular/core";

@Component({
  selector: "app-mod-preset-item",
  templateUrl: "./mod-preset-item.component.html",
  styleUrls: ["./mod-preset-item.component.scss"],
  standalone: false
})
export class ModPresetItemComponent {
  @Input() presetName: string = "";

  @Output() presetSelected = new EventEmitter<string>();
  @Output() presetDeleted = new EventEmitter<string>();
  @Output() presetSaved = new EventEmitter<string>();

  deletePreset($event: MouseEvent) {
    $event.stopPropagation();
    this.presetDeleted.emit(this.presetName);
  }

  savePreset($event: MouseEvent) {
    $event.stopPropagation();
    this.presetSaved.emit(this.presetName);
  }

  selectPreset() {
    this.presetSelected.emit(this.presetName);
  }
}
