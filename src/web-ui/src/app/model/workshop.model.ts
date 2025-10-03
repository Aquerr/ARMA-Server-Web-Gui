export interface WorkshopQueryRequest {
  cursor: string;
  searchText: string;
}

export interface WorkShopModInstallRequest {
  fileId: number;
  modName: string;
}

export interface WorkShopModInstallResponse {
  fileId: number;
}

export interface WorkshopQueryResponse {
  nextCursor: string;
  mods: WorkshopMod[];
}

export interface InstalledWorkshopItemsResponse {
  mods: WorkshopMod[];
  modsUnderInstallation: WorkShopModInstallRequest[];
}

export class WorkshopMod {
  description: string = "";
  fileId: number = 0;
  isBeingInstalled: boolean = false;
  modWorkshopUrl: string = "";
  previewUrl: string = "";
  title: string = "";
}

export interface WorkShopModInstallStatus {
  fileId: number;
  status: number;
}

export interface DownloadingMod {
  fileId: number;
  title: string;
  installAttemptCount: number;
  issuer: string;
}
