import { Component, OnInit } from '@angular/core';
import {ServerMissionsService} from "../../service/server-missions.service";
import {MaskService} from "../../service/mask.service";
import {NotificationService} from "../../service/notification.service";

@Component({
  selector: 'app-missions',
  templateUrl: './missions.component.html',
  styleUrls: ['./missions.component.css']
})
export class MissionsComponent implements OnInit {

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
      const formData = new FormData();
      formData.append("file", file);

      this.maskService.show();
      this.missionService.uploadMission(formData).subscribe((response) => {
        if (response.type == 4)
        {
          this.maskService.hide();
          this.notificationService.successNotification("Mission has been uploaded!");
        }
      },
        (error) => {
          this.maskService.hide();
          this.notificationService.errorNotification(error.error.message);
        });
    }
  }
}
