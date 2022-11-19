import {Component, OnInit} from '@angular/core';
import {MaskService} from "../../service/mask.service";
import {ServerStatusService} from "../../service/server-status.service";
import {ServerStatus} from "./model/status.model";
import {NotificationService} from "../../service/notification.service";

@Component({
  selector: 'app-status',
  templateUrl: './status.component.html',
  styleUrls: ['./status.component.css']
})
export class StatusComponent implements OnInit {

  serverStatus: ServerStatus = ServerStatus.OFFLINE;
  playerList: string[] = [];

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
  }

  private startServer() {
    this.serverStatusService.toggleServer({
      requestedStatus: ServerStatus.ONLINE
    }).subscribe(response => {
      this.notificationService.infoNotification("Server is starting...", "Server status");
    });
  }

  private stopServer() {
    this.serverStatusService.toggleServer({
      requestedStatus: ServerStatus.OFFLINE
    }).subscribe(response => {
      this.notificationService.infoNotification("Server is stopping...", "Server status");
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
