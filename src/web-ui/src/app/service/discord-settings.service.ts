import { inject, Injectable } from "@angular/core";
import { API_BASE_URL } from "../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { DiscordIntegrationSettings } from "../model/discord-settings.model";
import { Observable } from "rxjs";

@Injectable({
  providedIn: "root"
})
export class DiscordSettingsService {
  private readonly DISCORD_SETTINGS_URL = `${API_BASE_URL}/settings/discord`;
  private readonly httpClient = inject(HttpClient);

  constructor() {}

  getDiscordSettings(): Observable<DiscordIntegrationSettings> {
    return this.httpClient.get<DiscordIntegrationSettings>(this.DISCORD_SETTINGS_URL);
  }

  saveDiscordSettings(settings: DiscordIntegrationSettings) {
    return this.httpClient.post(this.DISCORD_SETTINGS_URL, settings);
  }
}
