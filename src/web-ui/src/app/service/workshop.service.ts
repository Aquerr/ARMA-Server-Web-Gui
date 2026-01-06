import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { API_BASE_URL } from "../../environments/environment";
import {
  DownloadingMod,
  InstalledWorkshopItemsResponse,
  WorkShopModInstallRequest,
  WorkShopModInstallResponse,
  WorkshopQueryRequest,
  WorkshopQueryResponse
} from "../model/workshop.model";
import { Observable } from "rxjs";

@Injectable({
  providedIn: "root"
})
export class WorkshopService {
  private readonly API_QUERY_WORKSHOP = `${API_BASE_URL}/workshop/query`;
  private readonly API_INSTALLED_ITEMS = `${API_BASE_URL}/workshop/installed-items`;
  private readonly API_INSTALL_MOD = `${API_BASE_URL}/workshop/install`;
  private readonly API_WORKSHOP_ACTIVE = `${API_BASE_URL}/workshop/active`;
  private readonly API_MOD_DOWNLOAD_QUEUE = `${API_BASE_URL}/workshop/download-queue`;

  constructor(private readonly httpClient: HttpClient) {}

  queryWorkshop(workshopQueryRequest: WorkshopQueryRequest): Observable<WorkshopQueryResponse> {
    return this.httpClient.post<WorkshopQueryResponse>(
      this.API_QUERY_WORKSHOP,
      workshopQueryRequest
    );
  }

  getInstalledWorkshopItems(): Observable<InstalledWorkshopItemsResponse> {
    return this.httpClient.get<InstalledWorkshopItemsResponse>(this.API_INSTALLED_ITEMS);
  }

  installMod(fileId: number, modName: string): Observable<WorkShopModInstallResponse> {
    const request = { fileId: fileId, modName: modName } as WorkShopModInstallRequest;
    return this.httpClient.post<WorkShopModInstallResponse>(this.API_INSTALL_MOD, request);
  }

  canUseWorkshop(): Observable<WorkshopActiveResponse> {
    return this.httpClient.get<WorkshopActiveResponse>(this.API_WORKSHOP_ACTIVE);
  }

  getModDownloadQueue(): Observable<ModDownloadQueueResponse> {
    return this.httpClient.get<ModDownloadQueueResponse>(this.API_MOD_DOWNLOAD_QUEUE);
  }
}

interface ModDownloadQueueResponse {
  mods: DownloadingMod[];
}

interface WorkshopActiveResponse {
  active: boolean;
}
