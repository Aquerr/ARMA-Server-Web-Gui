import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MaskService} from "../../service/mask.service";
import {ServerStatusService} from "../../service/server-status.service";
import {ServerStatus, Status} from "./model/status.model";
import {NotificationService} from "../../service/notification.service";
import {PlayerListComponent} from "./player-list/player-list.component";

@Component({
    selector: 'app-status',
    templateUrl: './status.component.html',
    styleUrls: ['./status.component.scss'],
    standalone: false
})
export class StatusComponent implements OnInit, OnDestroy {

  @ViewChild('playerListComponent') playerListComponent!: PlayerListComponent;

  serverStatus: ServerStatus = {status: Status.OFFLINE, statusText: "Offline"};

  refreshHandleId: number = 0;
  performUpdate: boolean = false;

  constructor(private maskService: MaskService,
              private notificationService: NotificationService,
              private serverStatusService: ServerStatusService) { }

  ngOnInit(): void {
    this.maskService.show();
    this.serverStatusService.getStatus().subscribe(response => {
      this.serverStatus = response.status;
      this.playerListComponent.playerList = response.playerList;
      this.maskService.hide();
    });

    this.refreshHandleId = setInterval(() => {
      this.refreshServerStatus();
    }, 5000);
  }

  ngOnDestroy(): void {
    clearInterval(this.refreshHandleId);
  }

  refreshServerStatus() {
    this.serverStatusService.getStatus().subscribe(response => {
      this.serverStatus = response.status;
      this.playerListComponent.playerList = response.playerList;
    });
  }

  getServerStatus() {
    return this.serverStatus;
  }

  toggleServer() {
    if (this.isServerOffline()) {
      this.startServer(this.performUpdate);
    } else {
      this.stopServer();
    }
    this.refreshServerStatus();
  }

  private startServer(performUpdate: boolean) {
    this.serverStatusService.toggleServer({
      requestedStatus: Status.ONLINE,
      performUpdate: performUpdate
    }).subscribe(response => {
      this.notificationService.infoNotification("Server is starting...", "Information");
    });
  }

  private stopServer() {
    this.serverStatusService.toggleServer({
      requestedStatus: Status.OFFLINE,
      performUpdate: false
    }).subscribe(response => {
      this.notificationService.infoNotification("Server is stopping...", "Information");
    });
  }

  isServerOffline(): boolean {
    return this.serverStatus.status == Status.OFFLINE;
  }

  isServerStarting(): boolean {
    return this.serverStatus.status == Status.STARTING || this.serverStatus.status == Status.UPDATING;
  }

  canToggleServer(): boolean {
    return this.serverStatus.status == Status.ONLINE
      || this.serverStatus.status == Status.RUNNING_BUT_NOT_DETECTED_BY_STEAM
      || this.isServerOffline();
  }
}
