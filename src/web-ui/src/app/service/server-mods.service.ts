import { HttpClient, HttpEvent } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Mod, ModPreset } from "@model/mod.model";
import { API_BASE_URL } from "@environments/environment";

@Injectable({
  providedIn: "root"
})
export class ServerModsService {
  private readonly MODS_URL = `${API_BASE_URL}/mods`;
  private readonly MODS_PRESETS_URL = `${API_BASE_URL}/mods-presets`;
  private readonly MODS_FILES_URL = `${API_BASE_URL}/mods-files`;

  constructor(private httpClient: HttpClient) {
  }

  public uploadMod(formData: FormData): Observable<HttpEvent<object>> {
    return this.httpClient.post(this.MODS_FILES_URL, formData, {
      reportUploadProgress: true,
      observe: "events"
    });
  }

  public checkModFilesExists(modFileName: string): Observable<DoesModExistsResponse> {
    return this.httpClient.get<DoesModExistsResponse>(
      `${this.MODS_FILES_URL}/${modFileName}/exists`
    );
  }

  public getInstalledMods(): Observable<GetModsResponse> {
    return this.httpClient.get<GetModsResponse>(this.MODS_URL);
  }

  public deleteMod(modName: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.MODS_URL}`, { body: { name: modName } });
  }

  public saveEnabledMods(saveEnabledModsRequest: SaveEnabledModsRequest) {
    return this.httpClient.post<void>(`${this.MODS_URL}/enabled`, saveEnabledModsRequest);
  }

  public manageMod(name: string) {
    return this.httpClient.post(`${this.MODS_URL}/manage`, { name: name });
  }

  public getModPresetByName(name: string): Observable<ModPreset> {
    return this.httpClient.get<ModPreset>(`${this.MODS_PRESETS_URL}`, {
      params: {
        name: name
      }
    }
    );
  }

  public getModPresetById(id: number): Observable<ModPreset> {
    return this.httpClient.get<ModPreset>(`${this.MODS_PRESETS_URL}`, {
      params: {
        id: id
      }
    }
    );
  }

  public getModPresetsNames(): Observable<ModPresetNamesResponse> {
    return this.httpClient.get<ModPresetNamesResponse>(`${this.MODS_PRESETS_URL}`, {
      params: {
        "names-only": true
      }
    });
  }

  public importPreset(request: ModPresetImportRequest) {
    return this.httpClient.post(`${this.MODS_PRESETS_URL}/import`, request);
  }

  public selectPreset(request: ModPresetSelectRequest) {
    return this.httpClient.post(`${this.MODS_PRESETS_URL}/select`, request);
  }

  public deletePreset(presetName: string): Observable<ModPresetDeleteResponse> {
    return this.httpClient.delete<ModPresetDeleteResponse>(
      `${this.MODS_PRESETS_URL}/${presetName}`
    );
  }

  public savePreset(request: ModPresetSaveRequest): Observable<ModPresetSaveResponse> {
    return this.httpClient.put<ModPresetSaveResponse>(
      `${this.MODS_PRESETS_URL}/${request.name}`,
      request
    );
  }

  public deleteNotManagedMod(directoryName: string) {
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
