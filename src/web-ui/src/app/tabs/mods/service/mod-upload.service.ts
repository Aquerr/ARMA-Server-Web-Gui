import { Injectable } from '@angular/core';
import {ServerModsService} from "../../../service/server-mods.service";
import {HttpEventType} from "@angular/common/http";
import {NotificationService} from "../../../service/notification.service";
import {Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ModUploadService {

  uploadingMods: {modName: string, progress: number, totalSize: number}[] = [];
  public modUploadedSubject!: Subject<any>;

  constructor(private serverModsService: ServerModsService,
              private notificationService: NotificationService) {
    this.modUploadedSubject = new Subject();
  }

  uploadMod(file: File) {

    if (!file.name.toLowerCase().endsWith(".zip") || file.type !== "application/x-zip-compressed") {
      this.notificationService.errorNotification("Wrong file type! Only .zip files are supported!");
      return;
    }

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

        if (response.type == HttpEventType.Response) {
          this.removeModWithName(file.name);
          this.notificationService.successNotification(`Mod ${file.name} has been uploaded!`);
          this.modUploadedSubject.next(null);
        }
      },
      error: (error) => {
        console.log(error);
        this.removeModWithName(file.name);
        this.notificationService.errorNotification(error.error.message);
      },
      complete: () => {
        this.removeModWithName(file.name);
      }
    });
  }

  getUploadingMods() {
    return this.uploadingMods;
  }

  removeModWithName(name: string) {
    const uploadingModIndex = this.uploadingMods.findIndex(uploadingMod => uploadingMod.modName === name);
    if (uploadingModIndex != -1) {
      this.uploadingMods.splice(uploadingModIndex, 1);
    }
  }
}
