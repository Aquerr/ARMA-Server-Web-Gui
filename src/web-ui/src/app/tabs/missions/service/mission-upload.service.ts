import { Injectable } from "@angular/core";
import { ServerMissionsService } from "../../../service/server-missions.service";
import { NotificationService } from "../../../service/notification.service";
import { Observable } from "rxjs";
import { FileUploadService } from "../../../service/file-upload.service";

@Injectable({
  providedIn: "root"
})
export class MissionUploadService extends FileUploadService {
  constructor(
    private missionService: ServerMissionsService,
    notificationService: NotificationService
  ) {
    super(notificationService, ["*"], [".pbo"]);
  }

  uploadMission(file: File) {
    this.uploadFile(file, false);
  }

  override doUpload(file: File): Observable<any> {
    const formData = new FormData();
    formData.append("file", file);
    return this.missionService.uploadMission(formData);
  }

  override doAfterUpload(file: File) {
    this.notificationService.successNotification(`Mission ${file.name} has been uploaded!`);
  }
}
