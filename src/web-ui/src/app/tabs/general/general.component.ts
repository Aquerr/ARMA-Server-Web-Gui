import { Component, OnInit, ViewChild } from "@angular/core";
import { MaskService } from "../../service/mask.service";
import { SaveGeneralProperties, ServerGeneralService } from "../../service/server-general.service";
import { NotificationService } from "../../service/notification.service";
import { MotdListComponent } from "./motd-list/motd-list.component";
import { MissionDifficulty } from "../../model/mission.model";
import { UnsafeService } from "../../service/unsafe.service";
import { OverwriteCommandlineParamsModalComponent } from "./unsafe/overwrite-commandline-params-modal/overwrite-commandline-params-modal.component";
import { DialogService } from "../../service/dialog.service";
import { PermissionService } from "../../service/permission.service";
import { AswgAuthority } from "../../model/authority.model";

@Component({
  selector: "app-general",
  templateUrl: "./general.component.html",
  styleUrls: ["./general.component.scss"],
  standalone: false
})
export class GeneralComponent implements OnInit {
  @ViewChild("motdListComponent") motdListComponent!: MotdListComponent;

  commandLineParams: string = "";
  canOverwriteCommandLineParams: boolean = false;
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

  constructor(
    private readonly permissionsService: PermissionService,
    private readonly maskService: MaskService,
    private readonly serverGeneralService: ServerGeneralService,
    private readonly notificationService: NotificationService,
    private readonly unsafeService: UnsafeService,
    private readonly dialogService: DialogService
  ) {}

  ngOnInit(): void {
    this.maskService.show();
    this.reloadGeneralProperties();
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

    this.serverGeneralService.saveGeneralProperties(saveGeneralProperties).subscribe((response) => {
      this.maskService.hide();
      this.notificationService.successNotification(
        "General settings have been updated!",
        "Success"
      );
    });
  }

  private reloadGeneralProperties() {
    this.serverGeneralService.getGeneralProperties().subscribe((response) => {
      this.commandLineParams = response.commandLineParams;
      this.canOverwriteCommandLineParams = response.canOverwriteCommandLineParams;
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

  public overrideCommandLineParams() {
    const onCloseCallback = (result: boolean) => {
      if (!result) return;

      const closeCallback = (result: string) => {
        if (result == "null") return;

        this.maskService.show();
        this.unsafeService.overwriteStartupParams(result).subscribe((response) => {
          this.maskService.hide();
          this.notificationService.successNotification("Updated commandline parameters");
          this.reloadGeneralProperties();
        });
      };
      this.dialogService.open(
        OverwriteCommandlineParamsModalComponent,
        closeCallback,
        this.commandLineParams,
        {
          width: "550px"
        }
      );
    };
    this.dialogService.openCommonConfirmationDialog(
      {
        question:
          "This is an unsafe feature. It is advised to not edit the command line directly. Are you sure you want to continue?"
      },
      onCloseCallback
    );
  }

  public formatCommandLineParams(commandLineParams: string) {
    return commandLineParams.replace(/\s/g, "\n");
  }

  public hasOverwriteCommandLineParamsPermission() {
    return this.permissionsService.hasAllAuthorities(
      [AswgAuthority.UNSAFE_OVERWRITE_STARTUP_PARAMS],
      false
    );
  }
}
