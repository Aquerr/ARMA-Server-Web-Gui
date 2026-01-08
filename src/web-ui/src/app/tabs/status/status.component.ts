import { Component, computed, DestroyRef, OnInit, signal, WritableSignal } from "@angular/core";
import { LoadingSpinnerMaskService } from "../../service/loading-spinner-mask.service";
import { ServerStatusService } from "../../service/server-status.service";
import { ServerStatus, Status } from "./model/status.model";
import { NotificationService } from "../../service/notification.service";
import { PlayerListComponent } from "./player-list/player-list.component";
import { ApiErrorCode, ApiErrorResponse } from "../../api/api-error.model";
import { DialogService } from "../../service/dialog.service";
import { HttpErrorResponse } from "@angular/common/http";
import { interval, switchMap } from "rxjs";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { ServerConsoleComponent } from "./server-console/server-console.component";
import { NgStyle } from "@angular/common";
import { MatCheckbox } from "@angular/material/checkbox";
import { FormsModule } from "@angular/forms";
import { MatButton } from "@angular/material/button";
import { ArmaServerPlayer } from "../../model/arma-server-player.model";

@Component({
  selector: "app-status",
  templateUrl: "./status.component.html",
  styleUrls: ["./status.component.scss"],
  imports: [ServerConsoleComponent, NgStyle, MatCheckbox, FormsModule, PlayerListComponent, MatButton]
})
export class StatusComponent implements OnInit {
  serverStatus: WritableSignal<ServerStatus> = signal<{ status: Status; statusText: string }>({ status: Status.OFFLINE, statusText: "Offline" });
  playerList: WritableSignal<ArmaServerPlayer[]> = signal<ArmaServerPlayer[]>([]);

  canToggleServer = computed(() => {
    return this.serverStatus().status == Status.ONLINE
      || this.serverStatus().status == Status.RUNNING_BUT_NOT_DETECTED_BY_STEAM
      || this.isServerOffline();
  });

  isServerOffline = computed(() => this.serverStatus().status == Status.OFFLINE);

  performUpdate: boolean = false;

  constructor(
    private maskService: LoadingSpinnerMaskService,
    private notificationService: NotificationService,
    private serverStatusService: ServerStatusService,
    private dialogService: DialogService,
    private destroyRef: DestroyRef
  ) {}

  ngOnInit(): void {
    this.maskService.show();
    this.serverStatusService.getStatus().subscribe((response) => {
      this.serverStatus.set(response.status);
      this.playerList.set(response.playerList);
      this.maskService.hide();
    });
  }

  refreshServerStatus() {
    interval(5000)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        switchMap(() => this.serverStatusService.getStatus())
      )
      .subscribe((response) => {
        this.serverStatus.set(response.status);
        this.playerList.set(response.playerList);
      });
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
        error: (err: HttpErrorResponse) => {
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
      .subscribe(() => {
        this.notificationService.infoNotification("Server is stopping...", "Information");
      });
  }
}
