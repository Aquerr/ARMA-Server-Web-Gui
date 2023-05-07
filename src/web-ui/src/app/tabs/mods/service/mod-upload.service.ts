import { Injectable } from '@angular/core';
import {ServerModsService} from "../../../service/server-mods.service";
import {HttpEventType} from "@angular/common/http";
import {NotificationService} from "../../../service/notification.service";

@Injectable({
  providedIn: 'root'
})
export class ModUploadService {

  uploadingMods: {modName: string, progress: number, totalSize: number}[] = [];

  constructor(private serverModsService: ServerModsService,
              private notificationService: NotificationService) { }

  uploadMod(file: File) {
    this.uploadingMods.push({modName: file.name, progress: 0, totalSize: file.size});
    const formData = new FormData();
    formData.append("file", file);
    this.serverModsService.uploadMod(formData).subscribe({
      next: (response) => {
        if (response.type == HttpEventType.UploadProgress) {
          const uploadingMod = this.uploadingMods.find(uploadingMod => uploadingMod.modName === file.name);
          if (uploadingMod) {
            uploadingMod.progress = Math.round(response.loaded / response.total * 100);
          }
        }

        if (response.type == HttpEventType.Response)
        {
          this.notificationService.successNotification(`Mod ${file.name} has been uploaded!`);
        }
      },
      error: (error) => {
        console.log(error);
        this.notificationService.errorNotification(error.error.message);
      },
      complete: () => {
        const uploadingModIndex = this.uploadingMods.findIndex(uploadingMod => uploadingMod.modName === file.name);
        if (uploadingModIndex) {
          this.uploadingMods.splice(uploadingModIndex, 1);
        }
      }
    });
  }

  getUploadingMods() {
    return this.uploadingMods;
  }
}
