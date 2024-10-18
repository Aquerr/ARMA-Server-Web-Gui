import {Component, Input} from '@angular/core';
import {ModSettings} from "../../../model/mod-settings.model";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-mod-settings-panel',
  templateUrl: './mod-settings-panel.component.html',
  styleUrl: './mod-settings-panel.component.css'
})
export class ModSettingsPanelComponent {

  @Input() modSettings!: ModSettings;

  constructor(private router: Router,
              private activeRoute: ActivatedRoute) {
  }

  onClick() {
    console.log(`Going to /mods-settings/${this.modSettings.id}`);
    this.router.navigate([`/mods-settings/${this.modSettings.id}`] );
  }
}
