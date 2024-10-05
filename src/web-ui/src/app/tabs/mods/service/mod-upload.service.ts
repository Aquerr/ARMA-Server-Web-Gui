import { Injectable } from '@angular/core';
import {ServerModsService} from "../../../service/server-mods.service";
import {NotificationService} from "../../../service/notification.service";
import {Observable} from "rxjs";
import {FileUploadService} from "../../../service/file-upload.service";

@Injectable({
  providedIn: 'root'
})
export class ModUploadService extends FileUploadService {

  constructor(private serverModsService: ServerModsService,
              notificationService: NotificationService) {
    super(notificationService, ["application/x-zip-compressed", "application/zip"], [".zip"]);
  }

  uploadMod(file: File) {
    this.doUpload(file);
  }

  upload(file: File): Observable<any> {
    const formData = new FormData();
    formData.append("file", file);
    return this.serverModsService.uploadMod(formData);
  }

  protected override doAfterUpload(file: File) {
    this.notificationService.successNotification(`Mod ${file.name} has been uploaded!`);
  }
}
