import { Component } from "@angular/core";
import { MatIcon } from "@angular/material/icon";
import { RouterLink } from "@angular/router";

@Component({
  selector: "app-settings",
  templateUrl: "./settings.component.html",
  imports: [
    MatIcon,
    RouterLink
  ],
  styleUrls: ["./settings.component.scss"]
})
export class SettingsComponent {}
