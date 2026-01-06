import { Injectable } from "@angular/core";
import { HttpClient, HttpEvent } from "@angular/common/http";
import { API_BASE_URL } from "../../environments/environment";
import { Observable } from "rxjs";
import { Mission } from "../model/mission.model";

@Injectable({
  providedIn: "root"
})
export class ServerMissionsService {
  private readonly MISSIONS_URL = `${API_BASE_URL}/missions`;
  private readonly MISSIONS_FILES_URL = `${API_BASE_URL}/missions-files`;

  constructor(private httpClient: HttpClient) {}

  addTemplateMission(name: string, template: string): Observable<object> {
    return this.httpClient.post(`${this.MISSIONS_URL}/template`, {
      name: name,
      template: template
    });
  }

  uploadMission(formData: FormData): Observable<HttpEvent<object>> {
    return this.httpClient.post(`${this.MISSIONS_FILES_URL}`, formData, {
      reportProgress: true,
      observe: "events"
    });
  }

  checkMissionFileExists(modFileName: string): Observable<DoesMissionExistsResponse> {
    return this.httpClient.get<DoesMissionExistsResponse>(
      `${this.MISSIONS_FILES_URL}/${modFileName}/exists`
    );
  }

  getInstalledMissions(): Observable<GetMissionsResponse> {
    return this.httpClient.get<GetMissionsResponse>(this.MISSIONS_URL);
  }

  deleteMission(template: string): Observable<object> {
    return this.httpClient.delete(`${this.MISSIONS_URL}/template`, {
      body: { template: template }
    });
  }

  saveEnabledMissions(saveEnabledMissionsRequest: SaveEnabledMissionsRequest): Observable<object> {
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

export interface DoesMissionExistsResponse {
  exists: boolean;
}
