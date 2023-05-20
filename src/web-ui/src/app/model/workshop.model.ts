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
}

export interface WorkshopMod {
  fileId: number;
  title: string;
  description: string;
  previewUrl: string;
  modWorkshopUrl: string;
}
