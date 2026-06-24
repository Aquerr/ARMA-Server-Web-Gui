import { Component, ChangeDetectionStrategy, output } from "@angular/core";
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
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ["./mod-upload-button.component.scss"]
})
export class ModUploadButtonComponent {
  public readonly fileSelected = output<File>();

  onFileSelected(event: Event) {
    const target = event.target as HTMLInputElement;

    if (!target.files) return;

    const file: File = target.files[0];
    if (file) {
      this.fileSelected.emit(file);
    }
  }
}
