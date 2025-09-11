import { inject, Injectable } from "@angular/core";
import { API_BASE_URL } from "../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { Cdlc } from "../model/cdlc.model";

@Injectable({
  providedIn: 'root'
})
export class CdlcService {

  private readonly CDLC_URL = `${API_BASE_URL}/cdlc`;
  private readonly httpClient = inject(HttpClient);

  constructor() { }

  getAllCdlcs(): Observable<GetCdlcListResponse> {
    return this.httpClient.get<GetCdlcListResponse>(this.CDLC_URL);
  }

  toggleCdlc(cdlcId: number) {
    return this.httpClient.post(`${this.CDLC_URL}/${cdlcId}/toggle`, {});
  }
}

export interface GetCdlcListResponse {
  cdlcs: Cdlc[];
}
