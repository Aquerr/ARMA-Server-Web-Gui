import { inject, Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { JobSettings } from "../model/job-settings.model";
import { API_BASE_URL } from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class JobSettingsService {

  private readonly JOBS_SETTINGS_URL = `${API_BASE_URL}/settings/jobs`;

  private readonly httpClient: HttpClient = inject(HttpClient);

  constructor() { }

  public getAllJobsNames(): Observable<string[]> {
    return this.httpClient.get<string[]>(`${this.JOBS_SETTINGS_URL}?names-only=true`)
  }

  public getJobSettings(name: string): Observable<JobSettings> {
    return this.httpClient.get<JobSettings>(`${this.JOBS_SETTINGS_URL}/${name}`);
  }

  public saveJobSettings(name: string, updateJobSettingsRequest: UpdateJobSettingsRequest): Observable<JobSettings> {
    return this.httpClient.put<JobSettings>(`${this.JOBS_SETTINGS_URL}/${name}`, updateJobSettingsRequest);
  }

  public runJobNow(name: string): Observable<void> {
    return this.httpClient.post<void>(`${this.JOBS_SETTINGS_URL}/${name}/run-now`, {});
  }
}

export interface UpdateJobSettingsRequest {
  enabled: boolean;
  cron: string;
  parameters: { [key: string]: string;};
}
