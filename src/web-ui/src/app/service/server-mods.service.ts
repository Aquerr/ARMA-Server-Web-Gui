import { HttpClient, HttpRequest } from '@angular/common/http';
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
}

export interface GetModsResponse{
  disabledMods: Mod[];
  enabledMods: Mod[];
}

export interface SaveEnabledModsRequest {
  mods: Mod[];
}
