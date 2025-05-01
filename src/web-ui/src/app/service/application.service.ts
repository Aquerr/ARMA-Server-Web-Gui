import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { ApplicationInfoResponse } from "../model/application.model";
import { API_BASE_URL } from "../../environments/environment";

@Injectable({
  providedIn: "root"
})
export class ApplicationService {
  private readonly ACTUATOR_URL = `${API_BASE_URL}/actuator`;

  constructor(private httpClient: HttpClient) {}

  getApplicationInfo(): Observable<ApplicationInfoResponse> {
    return this.httpClient.get<ApplicationInfoResponse>(`${this.ACTUATOR_URL}/info`);
  }
}
