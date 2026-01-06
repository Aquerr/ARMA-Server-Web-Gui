import { Component, inject } from "@angular/core";
import { MissionUploadService } from "../service/mission-upload.service";
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
  styleUrls: ["./mission-upload-button.component.scss"]
})
export class MissionUploadButtonComponent {
  private readonly missionUploadService = inject(MissionUploadService);

  onFileSelected(event: Event) {
    const target = event.target as HTMLInputElement;

    if (!target.files) return;

    const file: File = target.files[0];
    if (file) {
      this.missionUploadService.uploadMission(file);
    }
  }
}
