export interface WorkshopQueryRequest {
  cursor: string;
  searchText: string;
  searchByModId: boolean;
  sortingType: WorkshopSortingType;
  daysPeriod: number;
}

export interface WorkShopModInstallRequest {
  fileId: number;
  modName: string;
  installDependencies: boolean;
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
  progress = 0;
  modWorkshopUrl: string = "";
  previewUrl: string = "";
  title: string = "";
  dependencies: number[] = [];
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

export enum WorkshopSortingType {
  TEXT_RELEVANCE = "TEXT_RELEVANCE",
  POPULARITY = "POPULARITY",
  TOP_RATED = "TOP_RATED",
  PUBLICATION_DATE = "PUBLICATION_DATE",
  LAST_UPDATED = "LAST_UPDATED",
  MOST_SUBSCRIBERS = "MOST_SUBSCRIBERS"
}
