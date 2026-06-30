import { ChangeDetectionStrategy, Component, output } from "@angular/core";
import { MatTooltip } from "@angular/material/tooltip";
import { MatMiniFabButton } from "@angular/material/button";
import { MatIcon } from "@angular/material/icon";

@Component({
  selector: "app-upload-mission",
  templateUrl: "./mission-upload-button.component.html",
  imports: [
    MatTooltip,
    MatMiniFabButton,
    MatIcon
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ["./mission-upload-button.component.scss"]
})
export class MissionUploadButtonComponent {
  public fileSelected = output<File>();

  onFileSelected(event: Event) {
    const target = event.target as HTMLInputElement;

    if (!target.files) return;

    const file: File = target.files[0];
    if (file) {
      this.fileSelected.emit(file);
    }
  }
}
