export interface WorkshopQueryRequest {
  cursor: string;
  searchText: string;
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
