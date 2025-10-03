import { Component, OnDestroy, OnInit, ViewChild } from "@angular/core";
import { LoadingSpinnerMaskService } from "../../service/loading-spinner-mask.service";
import { ServerStatusService } from "../../service/server-status.service";
import { ServerStatus, Status } from "./model/status.model";
import { NotificationService } from "../../service/notification.service";
import { PlayerListComponent } from "./player-list/player-list.component";
import { ApiErrorCode, ApiErrorResponse } from "../../api/api-error.model";
import { DialogService } from "../../service/dialog.service";

@Component({
  selector: "app-status",
  templateUrl: "./status.component.html",
  styleUrls: ["./status.component.scss"],
  standalone: false
})
export class StatusComponent implements OnInit, OnDestroy {
  @ViewChild("playerListComponent") playerListComponent!: PlayerListComponent;

  serverStatus: ServerStatus = { status: Status.OFFLINE, statusText: "Offline" };

  refreshHandleId: number = 0;
  performUpdate: boolean = false;

  constructor(
    private maskService: LoadingSpinnerMaskService,
    private notificationService: NotificationService,
    private serverStatusService: ServerStatusService,
    private dialogService: DialogService
  ) {}

  ngOnInit(): void {
    this.maskService.show();
    this.serverStatusService.getStatus().subscribe((response) => {
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
    this.serverStatusService.getStatus().subscribe((response) => {
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
    this.serverStatusService
      .toggleServer({
        requestedStatus: Status.ONLINE,
        performUpdate: performUpdate
      })
      .subscribe({
        next: () => {
          this.notificationService.infoNotification("Server is starting...", "Information");
        },
        error: (err) => {
          const apiErrorResponse = err?.error as ApiErrorResponse;
          if (apiErrorResponse && apiErrorResponse.code === ApiErrorCode.SERVER_NOT_INSTALLED) {
            const onCloseCallback = (result: boolean) => {
              if (!result) return;

              this.notificationService.infoNotification(
                "Installing server files...",
                "Information"
              );
              this.maskService.show();
              this.serverStatusService
                .toggleServer({
                  requestedStatus: Status.ONLINE,
                  performUpdate: true
                })
                .subscribe({
                  next: () => {
                    this.maskService.hide();
                  }
                });
            };

            this.dialogService.openCommonConfirmationDialog(
              {
                question: `ARMA 3 server does not seem to be installed.
ASWG can try to install it, but it is recommended to do it manually. <br>Should ASWG try to install it?`
              },
              onCloseCallback
            );
          }
        }
      });
  }

  private stopServer() {
    this.serverStatusService
      .toggleServer({
        requestedStatus: Status.OFFLINE,
        performUpdate: false
      })
      .subscribe((response) => {
        this.notificationService.infoNotification("Server is stopping...", "Information");
      });
  }

  isServerOffline(): boolean {
    return this.serverStatus.status == Status.OFFLINE;
  }

  canToggleServer(): boolean {
    return (
      this.serverStatus.status == Status.ONLINE ||
      this.serverStatus.status == Status.RUNNING_BUT_NOT_DETECTED_BY_STEAM ||
      this.isServerOffline()
    );
  }
}
