export interface Mission {
  name: string;
  difficulty: MissionDifficulty;
  parameters: MissionParam[];
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
