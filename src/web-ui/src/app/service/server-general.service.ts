import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {API_BASE_URL} from "../../environments/environment";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ServerGeneralService {

  constructor(private httpClient: HttpClient) { }

  getGeneralProperties(): Observable<GeneralProperties> {
    return this.httpClient.get<GeneralProperties>(API_BASE_URL + "/general/properties");
  }

  saveGeneralProperties(saveGeneralProperties: SaveGeneralProperties) {
    return this.httpClient.post<void>(API_BASE_URL + "/general/properties", saveGeneralProperties);
  }
}

export interface SaveGeneralProperties {
  serverDirectory: string;
  maxPlayers: number;
  motd: string[];
}

export interface GeneralProperties {
  serverDirectory: string;
  maxPlayers: number;
  motd: string[];
}
