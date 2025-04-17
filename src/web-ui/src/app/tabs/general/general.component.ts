import {Component, OnInit, ViewChild} from '@angular/core';
import {MaskService} from "../../service/mask.service";
import {SaveGeneralProperties, ServerGeneralService} from "../../service/server-general.service";
import {NotificationService} from "../../service/notification.service";
import {MotdListComponent} from "./motd-list/motd-list.component";
import {MissionDifficulty} from "../../model/mission.model";

@Component({
    selector: 'app-general',
    templateUrl: './general.component.html',
    styleUrls: ['./general.component.scss'],
    standalone: false
})
export class GeneralComponent implements OnInit {

  @ViewChild('motdListComponent') motdListComponent!: MotdListComponent;

  commandLineParams: string = "";
  serverDirectory: string = "";
  modsDirectory: string = "";
  hostname: string = "";
  port: number = 2302;
  maxPlayers: number = 64;
  persistent: boolean = false;
  drawingInMap: boolean = true;
  headlessClients: string[] = [];
  localClients: string[] = [];
  forcedDifficulty: MissionDifficulty | null = null;

  constructor(private readonly maskService: MaskService,
              private readonly serverGeneralService: ServerGeneralService,
              private readonly notificationService: NotificationService) {
  }

  ngOnInit(): void {
    this.maskService.show();
    this.serverGeneralService.getGeneralProperties().subscribe(response => {
      this.commandLineParams = response.commandLineParams;
      this.serverDirectory = response.serverDirectory;
      this.modsDirectory = response.modsDirectory;
      this.port = response.port;
      this.hostname = response.hostname;
      this.maxPlayers = response.maxPlayers;
      this.motdListComponent.pupulateModtList(response.motd);
      this.motdListComponent.motdInterval = response.motdInterval;
      this.persistent = response.persistent;
      this.drawingInMap = response.drawingInMap;
      this.headlessClients = response.headlessClients;
      this.localClients = response.localClients;
      this.forcedDifficulty = response.forcedDifficulty;
      this.maskService.hide();
    });
  }

  save() {
    this.maskService.show();

    const saveGeneralProperties = {
      serverDirectory: this.serverDirectory,
      modsDirectory: this.modsDirectory,
      hostname: this.hostname,
      port: this.port,
      maxPlayers: this.maxPlayers,
      motd: this.motdListComponent.getMotdMessages(),
      motdInterval: this.motdListComponent.motdInterval,
      persistent: this.persistent,
      drawingInMap: this.drawingInMap,
      headlessClients: this.headlessClients,
      localClients: this.localClients,
      forcedDifficulty: this.forcedDifficulty
    } as SaveGeneralProperties;

    this.serverGeneralService.saveGeneralProperties(saveGeneralProperties).subscribe(response => {
      this.maskService.hide();
      this.notificationService.successNotification('General settings have been updated!', 'Success');
    });
  }
}
