import { Injectable } from "@angular/core";
import { ServerMissionsService } from "../../../service/server-missions.service";
import { NotificationService } from "../../../service/notification.service";
import { Observable } from "rxjs";
import { FileUploadService } from "../../../service/file-upload.service";
import { HttpEvent } from "@angular/common/http";

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

  uploadMission(file: File, overwrite: boolean = false) {
    this.uploadFile(file, overwrite);
  }

  override doUpload(file: File, overwrite: boolean = false): Observable<HttpEvent<object>> {
    const formData = new FormData();
    formData.append("file", file);
    formData.append("overwrite", overwrite.toString());
    return this.missionService.uploadMission(formData);
  }

  override doAfterUpload(file: File) {
    this.notificationService.successNotification(`Mission ${file.name} has been uploaded!`);
  }
}
