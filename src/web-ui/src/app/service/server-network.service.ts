import { Injectable } from '@angular/core';
import {API_BASE_URL} from '../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ServerNetworkService {

  private readonly NETWORK_URL = `${API_BASE_URL}/network`;
  private readonly NETWORK_PROPERTIES_URL = `${this.NETWORK_URL}/properties`

  constructor(private readonly httpClient: HttpClient) {}

  getServerNetworkProperties(): Observable<ServerNetworkProperties> {
    return this.httpClient.get<ServerNetworkProperties>(this.NETWORK_PROPERTIES_URL);
  }

  saveServerNetworkProperties(saveServerNetworkPropertiesRequest: ServerNetworkProperties): Observable<any> {
    return this.httpClient.post(this.NETWORK_PROPERTIES_URL, saveServerNetworkPropertiesRequest);
  }
}

export interface ServerNetworkProperties {
  upnp: boolean;
  maxPing: number;
  loopback: boolean;
  disconnectTimeout: number;
  maxDesync: number;
  maxPacketLoss: number;
  enablePlayerDiag: boolean;
  steamProtocolMaxDataSize: number;
  minBandwidth: number;
  maxBandwidth: number;
  maxMsgSend: number;
  maxSizeGuaranteed: number;
  maxSizeNonGuaranteed: number;
  minErrorToSend: number;
  minErrorToSendNear: number;
  maxCustomFileSize: number;
  maxPacketSize: number;
  kickTimeouts: KickTimeouts;
}

export interface KickTimeouts {
  manualKickTimeoutSeconds: number;
  connectivityKickTimeoutSeconds: number;
  battlEyeKickTimeoutSeconds: number;
  harmlessKickTimeoutSeconds: number;
}
