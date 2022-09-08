import { HttpClient, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ServerModsService {

  private readonly  MODS_URL = `${API_BASE_URL}/mods`

  constructor(private httpClient: HttpClient) { }

  uploadMission(formData: FormData): Observable<any> {
    const request = new HttpRequest('POST', this.MODS_URL, formData, {
      reportProgress: true
    });
    return this.httpClient.request(request);
  }

  getInstalledMods(): Observable<GetInstalledModsResponse>{
    return this.httpClient.get<GetInstalledModsResponse>(this.MODS_URL);
  }
}

export interface GetInstalledModsResponse{
  mods: string[];
}
