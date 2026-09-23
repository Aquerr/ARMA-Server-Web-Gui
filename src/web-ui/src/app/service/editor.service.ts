import { inject, Service } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { API_BASE_URL } from "@environments/environment";
import { Observable } from "rxjs";

@Service()
export class EditorService {
  private readonly EDITOR_URL = `${API_BASE_URL}/editor`;

  private readonly httpClient: HttpClient = inject(HttpClient);

  public loadFileContent(fileType: EditorFileType): Observable<FileContentResponse> {
    return this.httpClient.get<FileContentResponse>(`${this.EDITOR_URL}/${this.mapToUrlEndpoint(fileType)}`);
  }

  public saveFileContent(fileType: EditorFileType, content: string): Observable<void> {
    return this.httpClient.post<void>(`${this.EDITOR_URL}/${this.mapToUrlEndpoint(fileType)}`, {
      content: content
    } satisfies FileContentSaveRequest);
  }

  private mapToUrlEndpoint(fileType: EditorFileType): string {
    return fileType == EditorFileType.SERVER_CONFIG ? "server-config" : "network-config";
  }
}

export enum EditorFileType {
  SERVER_CONFIG = "SERVER_CONFIG",
  NETWORK_CONFIG = "NETWORK_CONFIG"
}

export interface FileContentResponse {
  content: string;
}

export interface FileContentSaveRequest {
  content: string;
}
