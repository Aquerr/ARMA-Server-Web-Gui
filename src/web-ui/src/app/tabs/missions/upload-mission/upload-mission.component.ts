import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {ServerMissionsService} from "../../../service/server-missions.service";
import {MaskService} from "../../../service/mask.service";
import {NotificationService} from "../../../service/notification.service";

@Component({
  selector: 'app-upload-mission',
  templateUrl: './upload-mission.component.html',
  styleUrls: ['./upload-mission.component.css']
})
export class UploadMissionComponent implements OnInit {

  @Output() missionUploaded: EventEmitter<any> = new EventEmitter<any>();

  constructor(private missionService: ServerMissionsService,
              private maskService: MaskService,
              private notificationService: NotificationService) { }

  ngOnInit(): void {
  }

  onFileSelected(event: Event) {
    console.log(event);

    const target = (event.target as HTMLInputElement);

    if (!target.files)
      return;

    const file : File = target.files[0];
    if (file)
    {
      this.uploadFile(file);
    }
  }

  uploadFile(file: File) {
    const formData = new FormData();
    formData.append("file", file);

    this.maskService.show();
    this.missionService.uploadMission(formData).subscribe((response) => {
        if (response.type == 4)
        {
          this.maskService.hide();
          this.missionUploaded.emit();
          this.notificationService.successNotification("Mission has been uploaded!");
        }
      },
      (error) => {
        this.maskService.hide();
        this.notificationService.errorNotification(error.error.message);
      });
  }

}
