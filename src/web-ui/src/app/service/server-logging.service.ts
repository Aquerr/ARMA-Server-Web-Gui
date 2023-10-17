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

  getLoggingSectionData(): Observable<LoggingProperties> {
    return this.httpClient.get<LoggingProperties>(this.LOGGING_URL);
  }

  saveLoggingSectionData(loggingSectionDataRequest: SaveLoggingPropertiesRequest): Observable<any> {
    return this.httpClient.post(this.LOGGING_URL, loggingSectionDataRequest);
  }

  pollServerLogsSse(eventSource: EventSource): Observable<string> {
    return new Observable(observer => {
      eventSource.onmessage = event => {
        observer.next(event.data);
      }
    });
  }
}

export interface LoggingProperties {
  logFile: string
}

export interface SaveLoggingPropertiesRequest {
  logFile: string
}
