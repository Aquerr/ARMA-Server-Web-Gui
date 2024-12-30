import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import { HttpClient } from "@angular/common/http";
import {API_BASE_URL} from "../../environments/environment";
import {ModSettings} from "../model/mod-settings.model";

@Injectable({
  providedIn: 'root'
})
export class ModSettingsService {

  private readonly MOD_SETTINGS_ROOT_URL = `${API_BASE_URL}/mods/settings`

  constructor(private httpClient: HttpClient) { }

  getAllModSettings(): Observable<ModSettings[]> {
    return this.httpClient.get<ModSettings[]>(this.MOD_SETTINGS_ROOT_URL);
  }

  getModSettings(id: number) {
    return this.httpClient.get<ModSettings>(`${this.MOD_SETTINGS_ROOT_URL}/${id}`);
  }

  deleteModSettings(id: number): Observable<any> {
    return this.httpClient.delete(`${this.MOD_SETTINGS_ROOT_URL}/${id}`);
  }

  updateModSettings(id: number, modSettings: ModSettings) {
    return this.httpClient.put(`${this.MOD_SETTINGS_ROOT_URL}/${id}`, modSettings);
  }

  getModSettingsContent(id: number): Observable<ModSettingsContent> {
    return this.httpClient.get<ModSettingsContent>(`${this.MOD_SETTINGS_ROOT_URL}/${id}/content`);
  }

  saveModSettingsContent(id: number, content: string): Observable<any> {
    return this.httpClient.put<any>(`${this.MOD_SETTINGS_ROOT_URL}/${id}/content`, {content: content} as ModSettingsContent);
  }

  createNewModSettings(modSettings: ModSettings) {
    return this.httpClient.post<ModSettings>(`${this.MOD_SETTINGS_ROOT_URL}`, modSettings);
  }
}

export interface ModSettingsContent {
  content: string;
}
