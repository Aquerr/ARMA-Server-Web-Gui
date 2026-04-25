export interface Mission {
  id: number;
  name: string;
  template: string;
  difficulty: MissionDifficulty;
  parameters: MissionParam[];
  sizeBytes: number;
}

export interface MissionParam {
  name: string;
  value: string;
}

export enum MissionDifficulty {
  RECRUIT = "RECRUIT",
  REGULAR = "REGULAR",
  VETERAN = "VETERAN",
  CUSTOM = "CUSTOM"
}
