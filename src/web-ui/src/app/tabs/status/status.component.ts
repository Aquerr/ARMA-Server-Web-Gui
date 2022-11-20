import {Component, OnInit} from '@angular/core';
import {MaskService} from "../../service/mask.service";
import {ServerStatusService} from "../../service/server-status.service";
import {ServerStatus} from "./model/status.model";
import {NotificationService} from "../../service/notification.service";
import {ArmaServerPlayer} from '../../model/arma-server-player.model';

@Component({
  selector: 'app-status',
  templateUrl: './status.component.html',
  styleUrls: ['./status.component.css']
})
export class StatusComponent implements OnInit {

  serverStatus: ServerStatus = ServerStatus.OFFLINE;
  playerList: ArmaServerPlayer[] = [];

  constructor(private maskService: MaskService,
              private notificationService: NotificationService,
              private serverStatusService: ServerStatusService) { }

  ngOnInit(): void {
    this.maskService.show();
    this.serverStatusService.getStatus().subscribe(response => {
      this.serverStatus = response.status;
      this.playerList = response.playerList;
      this.maskService.hide();
    });

    setInterval(() => {
      this.refreshServerStatus();
    }, 5000);
  }

  refreshServerStatus() {
    this.serverStatusService.getStatus().subscribe(response => {
      this.serverStatus = response.status;
      this.playerList = response.playerList;
    });
  }

  getServerStatus() {
    return this.serverStatus;
  }

  toggleServer() {
    if (this.isServerOffline()) {
      this.startServer();
    } else {
      this.stopServer();
    }
    this.refreshServerStatus();
  }

  private startServer() {
    this.serverStatusService.toggleServer({
      requestedStatus: ServerStatus.ONLINE
    }).subscribe(response => {
      this.notificationService.infoNotification("Server is starting...", "Information");
    });
  }

  private stopServer() {
    this.serverStatusService.toggleServer({
      requestedStatus: ServerStatus.OFFLINE
    }).subscribe(response => {
      this.notificationService.infoNotification("Server is stopping...", "Information");
    });
  }

  isServerOffline(): boolean {
    return this.serverStatus == ServerStatus.OFFLINE;
  }

  isServerStarting(): boolean {
    return this.serverStatus == ServerStatus.STARTING;
  }

  canToggleServer(): boolean {
    return this.serverStatus == ServerStatus.ONLINE || this.isServerOffline();
  }
}
