import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {API_BASE_URL} from "../../environments/environment";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ServerStatusService {

  private readonly API_SERVER_STATUS = `${API_BASE_URL}/status`;

  constructor(private httpClient: HttpClient) { }

  getStatus(): Observable<ServerStatusResponse> {
    return this.httpClient.get<ServerStatusResponse>(this.API_SERVER_STATUS);
  }
}

export interface ServerStatusResponse {
  status: string;
  playerList: string[];
}
