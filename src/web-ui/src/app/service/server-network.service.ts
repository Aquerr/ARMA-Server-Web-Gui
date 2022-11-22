import { Injectable } from '@angular/core';
import {API_BASE_URL} from '../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ServerNetworkService {

  private readonly NETWORK_URL = `${API_BASE_URL}/network`;
  private readonly NETWORK_PROPERTIES_URL = `${this.NETWORK_URL}/network/properties`

  constructor(private httpClient: HttpClient) {}

  getServerNetworkProperties(): Observable<GetServerNetworkProperties> {
    return this.httpClient.get<GetServerNetworkProperties>(this.NETWORK_PROPERTIES_URL);
  }

  saveServerNetworkProperties(saveServerNetworkPropertiesRequest: SaveServerNetworkProperties): Observable<any> {
    return this.httpClient.post(this.NETWORK_PROPERTIES_URL, saveServerNetworkPropertiesRequest);
  }
}

export interface SaveServerNetworkProperties {
  maxPing: number;
}

export interface GetServerNetworkProperties {
  maxPing: number;
}
