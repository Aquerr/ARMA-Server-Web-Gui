import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {API_BASE_URL} from "../../environments/environment";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ServerGeneralService {

  constructor(private httpClient: HttpClient) { }

  getServerDirectory(): Observable<GetServerDirectoryResponse> {
    return this.httpClient.get<GetServerDirectoryResponse>(API_BASE_URL + "/general/server-directory");
  }

  saveServerDirectory(serverDirectory: string) {
    return this.httpClient.post<void>(API_BASE_URL + "/general/server-directory",
      {path: serverDirectory} as UpdateServerDirectoryRequest);
  }
}

export interface UpdateServerDirectoryRequest {
  path: string;
}

export interface GetServerDirectoryResponse {
  path: string;
}
