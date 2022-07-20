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
}

export interface GetServerSecurityResponse {
  serverPassword: string;
  serverAdminPassword: string;
  serverCommandPassword: string;
}
