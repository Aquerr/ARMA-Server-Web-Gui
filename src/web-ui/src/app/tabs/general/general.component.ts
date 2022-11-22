import {Component, OnInit, ViewChild} from '@angular/core';
import {MaskService} from "../../service/mask.service";
import {SaveGeneralProperties, ServerGeneralService} from "../../service/server-general.service";
import {NotificationService} from "../../service/notification.service";
import {MotdListComponent} from "./motd-list/motd-list.component";

@Component({
  selector: 'app-general',
  templateUrl: './general.component.html',
  styleUrls: ['./general.component.css']
})
export class GeneralComponent implements OnInit {

  @ViewChild('motdListComponent') motdListComponent!: MotdListComponent;

  serverDirectory: string = "";
  hostname: string = "";
  maxPlayers: number = 64;
  persistent: boolean = false;

  constructor(private maskService: MaskService,
              private serverGeneralService: ServerGeneralService,
              private notificationService: NotificationService) {
  }

  ngOnInit(): void {
    this.maskService.show();
    this.serverGeneralService.getGeneralProperties().subscribe(response => {
      this.serverDirectory = response.serverDirectory;
      this.hostname = response.hostname;
      this.maxPlayers = response.maxPlayers;
      this.motdListComponent.motd = response.motd;
      this.motdListComponent.motdInterval = response.motdInterval;
      this.persistent = response.persistent;
      this.maskService.hide();
    });
  }

  save() {
    this.maskService.show();

    const saveGeneralProperties = {
      serverDirectory: this.serverDirectory,
      hostname: this.hostname,
      maxPlayers: this.maxPlayers,
      motd: this.motdListComponent.motd,
      motdInterval: this.motdListComponent.motdInterval,
      persistent: this.persistent
    } as SaveGeneralProperties;

    this.serverGeneralService.saveGeneralProperties(saveGeneralProperties).subscribe(response => {
      this.maskService.hide();
      this.notificationService.successNotification('General settings have been updated!');
    });
  }
}
