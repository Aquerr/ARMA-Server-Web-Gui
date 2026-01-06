import { Component, inject } from "@angular/core";
import { MissionUploadService } from "../service/mission-upload.service";

@Component({
  selector: "app-upload-mission",
  templateUrl: "./mission-upload-button.component.html",
  styleUrls: ["./mission-upload-button.component.scss"],
  standalone: false
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
