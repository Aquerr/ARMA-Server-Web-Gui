import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {API_BASE_URL} from "../../environments/environment";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ServerSecurityService {

  private readonly SECURITY_URL = `${API_BASE_URL}/security`;

  constructor(private httpClient: HttpClient) {}

  getServerSecurity(): Observable<GetServerSecurityResponse> {
    return this.httpClient.get<GetServerSecurityResponse>(this.SECURITY_URL);
  }

  saveServerSecurity(saveServerSecurityRequest: SaveServerSecurityRequest): Observable<any> {
    return this.httpClient.post(this.SECURITY_URL, saveServerSecurityRequest);
  }
}

export interface SaveServerSecurityRequest {
  serverPassword: string;
  serverAdminPassword: string;
  serverCommandPassword: string;
  battleEye: boolean;
}

export interface GetServerSecurityResponse {
  serverPassword: string;
  serverAdminPassword: string;
  serverCommandPassword: string;
  battleEye: boolean;
}
