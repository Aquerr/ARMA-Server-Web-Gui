import { Injectable } from '@angular/core';
import {HttpClient, HttpRequest} from "@angular/common/http";
import {API_BASE_URL} from "../../environments/environment";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ServerMissionsService {

  private readonly MISSIONS_URL = `${API_BASE_URL}/missions`;

  constructor(private httpClient: HttpClient) { }

  uploadMission(formData: FormData): Observable<any> {
    const request = new HttpRequest('POST', this.MISSIONS_URL, formData, {
      reportProgress: true
    });
    return this.httpClient.request(request);
  }

  getInstalledMissions(): Observable<GetInstalledMissionsResponse> {
    return this.httpClient.get<GetInstalledMissionsResponse>(this.MISSIONS_URL);
  }
}

export interface GetInstalledMissionsResponse {
  missions: string[];
}
