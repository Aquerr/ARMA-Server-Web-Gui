import { inject, Injectable } from "@angular/core";
import { API_BASE_URL } from "../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { SteamSettings } from "../model/steam-settings.model";

@Injectable({
  providedIn: 'root'
})
export class SteamSettingsService {

  private readonly STEAM_SETTINGS_URL = `${API_BASE_URL}/settings/steam`;
  private readonly httpClient = inject(HttpClient);

  getSteamSettings() {
    return this.httpClient.get<SteamSettings>(this.STEAM_SETTINGS_URL);

  }

  saveSteamSettings(settings: SteamSettings) {
    return this.httpClient.post(this.STEAM_SETTINGS_URL, settings);
  }
}
