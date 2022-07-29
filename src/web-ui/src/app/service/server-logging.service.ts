import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {API_BASE_URL} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ServerLoggingService {

  private readonly LOGGING_URL = `${API_BASE_URL}/logging/properties`;

  constructor(private httpClient: HttpClient) { }

  getLoggingSectionData(): Observable<LoggingSectionDataResponse> {
    return this.httpClient.get<LoggingSectionDataResponse>(this.LOGGING_URL);
  }

  saveLoggingSectionData(loggingSectionDataRequest: SaveLoggingSectionDataRequest): Observable<any> {
    return this.httpClient.post(this.LOGGING_URL, loggingSectionDataRequest);
  }
}

export interface LoggingSectionDataResponse {
  logFile: string
}

export interface SaveLoggingSectionDataRequest {
  logFile: string
}
