import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from 'src/environments/environment';
import {Mod} from "../model/mod.model";

@Injectable({
  providedIn: 'root'
})
export class ServerModsService {

  private readonly MODS_URL = `${API_BASE_URL}/mods`

  constructor(private httpClient: HttpClient) { }

  uploadMod(formData: FormData): Observable<any> {
    return this.httpClient.post(this.MODS_URL, formData, {
      reportProgress: true,
      observe: 'events'
    });
  }

  getInstalledMods(): Observable<GetModsResponse>{
    return this.httpClient.get<GetModsResponse>(this.MODS_URL);
  }

  deleteMod(modName: string): Observable<any> {
    return this.httpClient.delete(`${this.MODS_URL}/` + modName, {body: {}});
  }

  saveEnabledMods(saveEnabledModsRequest: SaveEnabledModsRequest) {
    return this.httpClient.post(`${this.MODS_URL}/enabled`, saveEnabledModsRequest);
  }

  getModPresetsNames(): Observable<ModPresetNamesResponse> {
    return this.httpClient.get<ModPresetNamesResponse>(`${this.MODS_URL}/presets-names`);
  }

  importPreset(request: ModPresetImportRequest) {
    return this.httpClient.post(`${this.MODS_URL}/presets/import`, request);
  }

  selectPreset(request: ModPresetSelectRequest) {
    return this.httpClient.post(`${this.MODS_URL}/presets/select`, request);
  }

  deletePreset(presetName: string): Observable<ModPresetDeleteResponse> {
    return this.httpClient.delete<ModPresetDeleteResponse>(`${this.MODS_URL}/presets/${presetName}`);
  }

  savePreset(request: ModPresetSaveRequest): Observable<ModPresetSaveResponse> {
    return this.httpClient.put<ModPresetSaveResponse>(`${this.MODS_URL}/presets/${request.name}`, request);
  }
}

export interface GetModsResponse{
  disabledMods: Mod[];
  enabledMods: Mod[];
}

export interface SaveEnabledModsRequest {
  mods: Mod[];
}

export interface ModPresetNamesResponse {
  presets: string[];
}

export interface ModPresetImportRequest {
  name: string;
  modParams: ModPresetModParam[];
}

export interface ModPresetModParam {
  title: string;
  id: number;
}

export interface ModPresetSelectRequest {
  name: string;
}

export interface ModPresetDeleteResponse {
  deleted: boolean;
}

export interface ModPresetSaveRequest {
  name: string;
  modNames: string[];
}

export interface ModPresetSaveResponse {
  saved: boolean;
}
