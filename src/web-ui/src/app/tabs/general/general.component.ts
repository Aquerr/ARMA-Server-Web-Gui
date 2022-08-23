import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {MaskService} from "../../service/mask.service";
import {SaveGeneralProperties, ServerGeneralService} from "../../service/server-general.service";
import {NotificationService} from "../../service/notification.service";

@Component({
  selector: 'app-general',
  templateUrl: './general.component.html',
  styleUrls: ['./general.component.css']
})
export class GeneralComponent implements OnInit {

  // @ViewChild("motdList") motdListElement: ElementRef;

  motd: string[] = [];

  serverDirectory: string = "";
  maxPlayers: number = 64;

  motdLine: string = "";

  constructor(private maskService: MaskService,
              private serverGeneralService: ServerGeneralService,
              private notificationService: NotificationService) {
  }

  ngOnInit(): void {
    this.maskService.show();
    this.serverGeneralService.getGeneralProperties().subscribe(response => {
      this.serverDirectory = response.serverDirectory;
      this.maxPlayers = response.maxPlayers;
      this.motd = response.motd;
      this.maskService.hide();
    });
  }

  save() {
    this.maskService.show();

    const saveGeneralProperties = {
      serverDirectory: this.serverDirectory,
      maxPlayers: this.maxPlayers,
      motd: this.motd
    } as SaveGeneralProperties;

    this.serverGeneralService.saveGeneralProperties(saveGeneralProperties).subscribe(response => {
      this.maskService.hide();
      this.notificationService.successNotification('Server directory has been updated!');
    });
  }
}
