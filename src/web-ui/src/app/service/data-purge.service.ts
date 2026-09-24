import { inject, Service } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { API_BASE_URL } from "@environments/environment";
import { Observable } from "rxjs";

@Service()
export class DataPurgeService {
  private readonly httpClient = inject(HttpClient);

  public purgeModPresets(): Observable<void> {
    return this.httpClient.delete<void>(`${API_BASE_URL}/settings/purge/mod-presets`);
  }

  public purgeMissions(deleteFiles: boolean): Observable<void> {
    return this.httpClient.delete<void>(`${API_BASE_URL}/settings/purge/missions`, {
      params: {
        "delete-files": deleteFiles
      }
    });
  }

  public purgeMods(deleteFiles: boolean): Observable<void> {
    return this.httpClient.delete<void>(`${API_BASE_URL}/settings/purge/mods`, {
      params: {
        "delete-files": deleteFiles
      }
    });
  }
}
