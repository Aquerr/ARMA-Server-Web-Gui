import {Component} from '@angular/core';
import {ModSettings} from "../../model/mod-settings.model";
import {ModSettingsService} from "../../service/mod-settings.service";
import {MaskService} from "../../service/mask.service";

@Component({
  selector: 'app-mods-settings',
  templateUrl: './mods-settings.component.html',
  styleUrl: './mods-settings.component.css'
})
export class ModsSettingsComponent {

  modSettingsList: ModSettings[] = [];

  constructor(private modSettingsService: ModSettingsService,
              private maskService: MaskService) {

    this.maskService.show();
    this.modSettingsService.getAllModSettings().subscribe(response => {
      this.modSettingsList = response;
      this.maskService.hide();
    });
  }

  addNewModSettings() {

  }

  saveAll() {

  }

  onModSettingsDelete(event: any) {

  }

  onModSettingsSave(event: any) {

  }

  onModSettingsActivate(event: any) {

  }
}
