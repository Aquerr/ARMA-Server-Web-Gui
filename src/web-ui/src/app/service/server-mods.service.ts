import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { API_BASE_URL } from "src/environments/environment";
import { Mod } from "../model/mod.model";

@Injectable({
  providedIn: "root"
})
export class ServerModsService {
  private readonly MODS_URL = `${API_BASE_URL}/mods`;
  private readonly MODS_PRESETS_URL = `${API_BASE_URL}/mods-presets`;
  private readonly MODS_FILES_URL = `${API_BASE_URL}/mods-files`;

  constructor(private httpClient: HttpClient) {}

  uploadMod(formData: FormData): Observable<any> {
    return this.httpClient.post(this.MODS_FILES_URL, formData, {
      reportProgress: true,
      observe: "events"
    });
  }

  checkModFilesExists(modFileName: string): Observable<DoesModExistsResponse> {
    return this.httpClient.get<DoesModExistsResponse>(
      `${this.MODS_FILES_URL}/${modFileName}/exists`
    );
  }

  getInstalledMods(): Observable<GetModsResponse> {
    return this.httpClient.get<GetModsResponse>(this.MODS_URL);
  }

  deleteMod(modName: string): Observable<any> {
    return this.httpClient.delete(`${this.MODS_URL}`, { body: { name: modName } });
  }

  saveEnabledMods(saveEnabledModsRequest: SaveEnabledModsRequest) {
    return this.httpClient.post(`${this.MODS_URL}/enabled`, saveEnabledModsRequest);
  }

  manageMod(name: string) {
    return this.httpClient.post(`${this.MODS_URL}/manage`, { name: name });
  }

  getModPresetsNames(): Observable<ModPresetNamesResponse> {
    return this.httpClient.get<ModPresetNamesResponse>(`${this.MODS_PRESETS_URL}`);
  }

  importPreset(request: ModPresetImportRequest) {
    return this.httpClient.post(`${this.MODS_PRESETS_URL}/import`, request);
  }

  selectPreset(request: ModPresetSelectRequest) {
    return this.httpClient.post(`${this.MODS_PRESETS_URL}/select`, request);
  }

  deletePreset(presetName: string): Observable<ModPresetDeleteResponse> {
    return this.httpClient.delete<ModPresetDeleteResponse>(
      `${this.MODS_PRESETS_URL}/${presetName}`
    );
  }

  savePreset(request: ModPresetSaveRequest): Observable<ModPresetSaveResponse> {
    return this.httpClient.put<ModPresetSaveResponse>(
      `${this.MODS_PRESETS_URL}/${request.name}`,
      request
    );
  }

  deleteNotManagedMod(directoryName: string) {
    return this.httpClient.delete(`${this.MODS_URL}/not-managed`, { body: { directoryName: directoryName } });
  }
}

export interface GetModsResponse {
  disabledMods: Mod[];
  enabledMods: Mod[];
  notManagedMods: Mod[];
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

export interface DoesModExistsResponse {
  exists: boolean;
}
