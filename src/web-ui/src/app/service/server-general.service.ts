import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
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
  hostname: string;
  serverDirectory: string;
  modsDirectory: string;
  port: number;
  maxPlayers: number;
  motd: string[];
  motdInterval: number;
  persistent: boolean;
  drawingInMap: boolean;
  headlessClients: string[];
  localClients: string[];
}

export interface GeneralProperties {
  serverDirectory: string;
  modsDirectory: string;
  commandLineParams: string;
  port: number;
  hostname: string;
  maxPlayers: number;
  motd: string[];
  motdInterval: number;
  persistent: boolean;
  drawingInMap: boolean;
  headlessClients: string[];
  localClients: string[];
}
