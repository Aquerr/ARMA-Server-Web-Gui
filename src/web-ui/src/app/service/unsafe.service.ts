import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { API_BASE_URL } from "../../environments/environment";
import { Observable } from "rxjs";

@Injectable({
  providedIn: "root"
})
export class UnsafeService {
  private readonly UNSAFE_URL = `${API_BASE_URL}/unsafe`;

  constructor(private httpClient: HttpClient) { }

  public overwriteStartupParams(params: string): Observable<void> {
    return this.httpClient.post<void>(`${this.UNSAFE_URL}/startup-params`, { params: params });
  }
}
