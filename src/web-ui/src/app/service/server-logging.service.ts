import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { HttpClient } from "@angular/common/http";
import { API_BASE_URL } from "../../environments/environment";

@Injectable({
  providedIn: "root"
})
export class ServerLoggingService {
  private readonly LOGGING_URL = `${API_BASE_URL}/logging/properties`;

  constructor(private httpClient: HttpClient) {}

  getLoggingSectionData(): Observable<LoggingProperties> {
    return this.httpClient.get<LoggingProperties>(this.LOGGING_URL);
  }

  saveLoggingSectionData(loggingSectionDataRequest: SaveLoggingPropertiesRequest): Observable<void> {
    return this.httpClient.post<void>(this.LOGGING_URL, loggingSectionDataRequest);
  }

  getLatestServerLogs(): Observable<LatestServerLogs> {
    return this.httpClient.get<LatestServerLogs>(`${API_BASE_URL}/logging/latest-logs`);
  }
}

export interface LoggingProperties {
  logFile: string;
}

export interface SaveLoggingPropertiesRequest {
  logFile: string;
}

export interface LatestServerLogs {
  logs: string[];
}
