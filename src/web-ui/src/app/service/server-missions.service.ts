import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {API_BASE_URL} from "../../environments/environment";
import {Observable} from "rxjs";
import {Mission} from "../model/mission.model";

@Injectable({
  providedIn: 'root'
})
export class ServerMissionsService {

  private readonly MISSIONS_URL = `${API_BASE_URL}/missions`;

  constructor(private httpClient: HttpClient) { }

  addTemplateMission(name: string, template: string): Observable<any> {
    return this.httpClient.post(`${this.MISSIONS_URL}/template`, {name: name, template: template});
  }

  uploadMission(formData: FormData): Observable<any> {
    return this.httpClient.post(`${this.MISSIONS_URL}/file`, formData, {
      reportProgress: true,
      observe: 'events'
    });
  }

  getInstalledMissions(): Observable<GetMissionsResponse> {
    return this.httpClient.get<GetMissionsResponse>(this.MISSIONS_URL);
  }

  deleteMission(template: string): Observable<any> {
    return this.httpClient.delete(`${this.MISSIONS_URL}/template/` + template, {body: {}});
  }

  saveEnabledMissions(saveEnabledMissionsRequest: SaveEnabledMissionsRequest): Observable<any> {
    return this.httpClient.post(`${this.MISSIONS_URL}/enabled`, saveEnabledMissionsRequest);
  }

  updateMission(id: number, mission: Mission) {
    return this.httpClient.put(`${this.MISSIONS_URL}/id/${id}`, mission);
  }
}

export interface GetMissionsResponse {
  disabledMissions: Mission[];
  enabledMissions: Mission[];
}

export interface SaveEnabledMissionsRequest {
  missions: Mission[];
}
