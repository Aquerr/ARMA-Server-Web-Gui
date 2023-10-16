import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {API_BASE_URL} from "../../environments/environment";
import {Observable} from "rxjs";
import {ServerStatus, Status} from "../tabs/status/model/status.model";
import {ArmaServerPlayer} from '../model/arma-server-player.model';

@Injectable({
  providedIn: 'root'
})
export class ServerStatusService {

  private readonly API_SERVER_STATUS = `${API_BASE_URL}/status`;
  private readonly API_SERVER_STATUS_TOGGLE = `${this.API_SERVER_STATUS}/toggle`;

  constructor(private httpClient: HttpClient) { }

  getStatus(): Observable<ServerStatusResponse> {
    return this.httpClient.get<ServerStatusResponse>(this.API_SERVER_STATUS);
  }

  toggleServer(toggleServerRequest: ToggleServerRequest): Observable<void> {
    return this.httpClient.post<void>(this.API_SERVER_STATUS_TOGGLE, toggleServerRequest);
  }
}

export interface ToggleServerRequest {
  requestedStatus: Status;
  performUpdate: boolean;
}

export interface ServerStatusResponse {
  status: ServerStatus;
  playerList: ArmaServerPlayer[];
}
