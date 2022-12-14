export interface Mission {
  name: string;
  parameters: MissionParam[];
}

export interface MissionParam {
  name: string;
  value: string;
}
