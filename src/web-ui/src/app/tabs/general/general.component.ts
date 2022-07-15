import { Component, OnInit } from '@angular/core';
import {MaskService} from "../../service/mask.service";
import {ServerGeneralService} from "../../service/server-general.service";
import {NotificationService} from "../../service/notification.service";

@Component({
  selector: 'app-general',
  templateUrl: './general.component.html',
  styleUrls: ['./general.component.css']
})
export class GeneralComponent implements OnInit {

  serverDirectory: string = "";

  constructor(private maskService: MaskService,
              private serverGeneralService: ServerGeneralService,
              private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.maskService.show();
    this.serverGeneralService.getServerDirectory().subscribe(response => {
      this.serverDirectory = response.path;
      this.maskService.hide();
    });
  }

  save() {
    this.maskService.show();
    this.serverGeneralService.saveServerDirectory(this.serverDirectory).subscribe(response => {
      this.maskService.hide();
      this.notificationService.successNotification('Server directory has been updated!');
    });
  }
}
