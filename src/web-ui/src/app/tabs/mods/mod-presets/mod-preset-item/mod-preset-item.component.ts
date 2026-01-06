import { Component, EventEmitter, Input, Output } from "@angular/core";
import { MatIconButton } from "@angular/material/button";
import { MatIcon } from "@angular/material/icon";

@Component({
  selector: "app-mod-preset-item",
  templateUrl: "./mod-preset-item.component.html",
  imports: [
    MatIconButton,
    MatIcon
  ],
  styleUrls: ["./mod-preset-item.component.scss"]
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
