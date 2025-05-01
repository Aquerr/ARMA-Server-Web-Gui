import { Component, EventEmitter, Output } from "@angular/core";

@Component({
  selector: "app-upload-mod",
  templateUrl: "./mod-upload-button.component.html",
  styleUrls: ["./mod-upload-button.component.scss"],
  standalone: false
})
export class ModUploadButtonComponent {
  @Output() fileSelected: EventEmitter<File> = new EventEmitter<File>();

  constructor() {}

  onFileSelected(event: Event) {
    const target = event.target as HTMLInputElement;

    if (!target.files) return;

    const file: File = target.files[0];
    if (file) {
      this.fileSelected.emit(file);
    }
  }
}
