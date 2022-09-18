import { HttpClient, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ServerModsService {

  private readonly MODS_URL = `${API_BASE_URL}/mods`

  constructor(private httpClient: HttpClient) { }

  uploadMission(formData: FormData): Observable<any> {
    const request = new HttpRequest('POST', this.MODS_URL, formData, {
      reportProgress: true
    });
    return this.httpClient.request(request);
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
  disabledMods: string[];
  enabledMods: string[];
}

export interface SaveEnabledModsRequest {
  mods: string[];
}
