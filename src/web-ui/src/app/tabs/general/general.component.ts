import { Component, OnInit } from '@angular/core';
import {MaskService} from "../../service/mask.service";
import {SaveGeneralProperties, ServerGeneralService} from "../../service/server-general.service";
import {NotificationService} from "../../service/notification.service";

@Component({
  selector: 'app-general',
  templateUrl: './general.component.html',
  styleUrls: ['./general.component.css']
})
export class GeneralComponent implements OnInit {

  serverDirectory: string = "";
  maxPlayers: number = 64;

  constructor(private maskService: MaskService,
              private serverGeneralService: ServerGeneralService,
              private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.maskService.show();
    this.serverGeneralService.getGeneralProperties().subscribe(response => {
      this.serverDirectory = response.serverDirectory;
      this.maxPlayers = response.maxPlayers;
      this.maskService.hide();
    });
  }

  save() {
    this.maskService.show();

    const saveGeneralProperties = {
      path: this.serverDirectory,
      maxPlayers: this.maxPlayers
    } as SaveGeneralProperties;

    this.serverGeneralService.saveGeneralProperties(saveGeneralProperties).subscribe(response => {
      this.maskService.hide();
      this.notificationService.successNotification('Server directory has been updated!');
    });
  }
}
