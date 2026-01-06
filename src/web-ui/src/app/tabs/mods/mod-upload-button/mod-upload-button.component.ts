import { Component, EventEmitter, Output } from "@angular/core";
import { MatTooltip } from "@angular/material/tooltip";
import { MatIcon } from "@angular/material/icon";
import { MatMiniFabButton } from "@angular/material/button";

@Component({
  selector: "app-upload-mod",
  templateUrl: "./mod-upload-button.component.html",
  imports: [
    MatTooltip,
    MatIcon,
    MatMiniFabButton
  ],
  styleUrls: ["./mod-upload-button.component.scss"]
})
export class ModUploadButtonComponent {
  @Output() fileSelected: EventEmitter<File> = new EventEmitter<File>();

  onFileSelected(event: Event) {
    const target = event.target as HTMLInputElement;

    if (!target.files) return;

    const file: File = target.files[0];
    if (file) {
      this.fileSelected.emit(file);
    }
  }
}
