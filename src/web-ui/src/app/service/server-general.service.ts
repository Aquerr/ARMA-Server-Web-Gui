import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { API_BASE_URL } from "../../environments/environment";
import { Observable, throwError } from "rxjs";
import { PermissionService } from "./permission.service";
import { AswgAuthority } from "../model/authority.model";
import { MissionDifficulty } from "../model/mission.model";

@Injectable({
  providedIn: "root"
})
export class ServerGeneralService {
  constructor(
    private readonly httpClient: HttpClient,
    private readonly permissionService: PermissionService
  ) {}

  getGeneralProperties(): Observable<GeneralProperties> {
    if (!this.permissionService.hasAllAuthorities([AswgAuthority.GENERAL_SETTINGS_VIEW], true))
      return throwError(() => new Error("Access denied"));

    return this.httpClient.get<GeneralProperties>(API_BASE_URL + "/general/properties");
  }

  saveGeneralProperties(saveGeneralProperties: SaveGeneralProperties) {
    // TODO: Add observable methods to permissionservice that can be piped
    if (!this.permissionService.hasAllAuthorities([AswgAuthority.GENERAL_SETTINGS_SAVE], true))
      return throwError(() => new Error("Access denied"));

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
  forcedDifficulty: MissionDifficulty | null;
  branch: string;
}

export interface GeneralProperties {
  serverDirectory: string;
  modsDirectory: string;
  commandLineParams: string;
  canOverwriteCommandLineParams: boolean;
  port: number;
  hostname: string;
  maxPlayers: number;
  motd: string[];
  motdInterval: number;
  persistent: boolean;
  drawingInMap: boolean;
  headlessClients: string[];
  localClients: string[];
  forcedDifficulty: MissionDifficulty | null;
  branch: string;
}
