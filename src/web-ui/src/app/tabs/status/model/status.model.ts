export interface ServerStatus {
  status: Status;
  statusText: string;
}

export enum Status {
  ONLINE = "ONLINE",
  RUNNING_BUT_NOT_DETECTED_BY_STEAM = "RUNNING_BUT_NOT_DETECTED_BY_STEAM",
  OFFLINE = "OFFLINE",
  STARTING = "STARTING",
  UPDATING = "UPDATING"
}
