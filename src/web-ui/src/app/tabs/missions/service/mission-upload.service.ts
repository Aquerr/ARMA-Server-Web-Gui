import {Injectable} from '@angular/core';
import {HttpEventType} from "@angular/common/http";
import {ServerMissionsService} from "../../../service/server-missions.service";
import {NotificationService} from "../../../service/notification.service";
import {Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class MissionUploadService {

  uploadingMissions: {missionName: string, progress: number, totalSize: number}[] = [];
  public missionUploadedSubject!: Subject<any>;

  constructor(private missionService: ServerMissionsService,
              private notificationService: NotificationService) {
    this.missionUploadedSubject = new Subject();
  }

  uploadMission(file: File) {

    if (!file.name.toLowerCase().endsWith(".pbo") && file.type !== "") {
      this.notificationService.errorNotification("Wrong file type! Only .pbo files are supported!");
      return;
    }

    this.uploadingMissions.push({missionName: file.name, progress: 0, totalSize: file.size});
    const formData = new FormData();
    formData.append("file", file);
    this.missionService.uploadMission(formData).subscribe({
      next: (response) => {
        if (response.type == HttpEventType.UploadProgress) {
          const uploadingMission = this.uploadingMissions.find(uploadingMission => uploadingMission.missionName === file.name);
          if (uploadingMission) {
            uploadingMission.progress = Math.round(response.loaded / response.total * 100);
          }
        }

        if (response.type == HttpEventType.Response) {
          this.removeMissionWithName(file.name);
          this.notificationService.successNotification(`Mission ${file.name} has been uploaded!`);
          this.missionUploadedSubject.next(null);
        }
      },
      error: (error) => {
        console.log(error);
        this.removeMissionWithName(file.name);
        this.notificationService.errorNotification(error.error.message);
      },
      complete: () => {
        this.removeMissionWithName(file.name);
      }
    });
  }

  getUploadingMissions() {
    return this.uploadingMissions;
  }

  removeMissionWithName(name: string) {
    const uploadingMissionIndex = this.uploadingMissions.findIndex(uploadingMission => uploadingMission.missionName === name);
    if (uploadingMissionIndex != -1) {
      this.uploadingMissions.splice(uploadingMissionIndex, 1);
    }
  }
}
