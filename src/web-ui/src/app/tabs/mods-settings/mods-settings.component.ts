import { Component, QueryList, ViewChildren } from "@angular/core";
import { ModSettings } from "../../model/mod-settings.model";
import { ModSettingsService } from "../../service/mod-settings.service";
import { MaskService } from "../../service/mask.service";
import { NotificationService } from "../../service/notification.service";
import { DialogService } from "../../service/dialog.service";
import { ModSettingsPanelComponent } from "./mod-settings-panel/mod-settings-panel.component";

@Component({
  selector: "app-mods-settings",
  templateUrl: "./mods-settings.component.html",
  styleUrl: "./mods-settings.component.scss",
  standalone: false
})
export class ModsSettingsComponent {
  modSettingsList: ModSettings[] = [];

  @ViewChildren("modSettingPanels") modSettingPanels!: QueryList<ModSettingsPanelComponent>;

  constructor(
    private readonly modSettingsService: ModSettingsService,
    private readonly maskService: MaskService,
    private readonly notificationService: NotificationService,
    private readonly dialogService: DialogService
  ) {
    this.reloadModSettings();
  }

  private reloadModSettings() {
    this.maskService.show();
    this.modSettingsService.getAllModSettings().subscribe((response) => {
      this.modSettingsList = response;
      this.maskService.hide();
    });
  }

  addNewModSettings() {
    const modSettings = {
      name: "???",
      active: false
    } as ModSettings;
    this.modSettingsList.push(modSettings);
  }

  onModSettingsDelete(id: number) {
    if (!id) {
      this.reloadModSettings();
      return;
    }

    const onCloseCallback = (result: boolean) => {
      if (!result) return;
      this.maskService.show();
      this.modSettingsService.deleteModSettings(id).subscribe((response) => {
        this.notificationService.successNotification("Mod settings have been deleted");
        this.reloadModSettings();
      });
    };

    this.dialogService.openCommonConfirmationDialog(
      {
        question: "Are you sure you want to delete this mod settings?"
      },
      onCloseCallback
    );
  }

  onModSettingsActivate(modSettings: ModSettings) {
    if (modSettings.active) {
      this.modSettingPanels.forEach((panel) => {
        if (panel.getName() != modSettings.name) {
          panel.formService.getActiveControl(panel.form).setValue(false);
        }
      });
    }
  }
}
