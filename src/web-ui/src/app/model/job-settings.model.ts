export interface JobSettings {
  enabled: string;
  cron: string;
  parameters: [{name: string, description: string, value: string}];
  lastExecutionDate: string;
  lastExecutionFinishedDate: string;
  nextExecutionDate: string;
  lastMessage: string;
  lastStatus: string;
}

export enum JobStatus {
  STARTED = 'STARTED',
  SUCCESS = 'SUCCESS',
  FAILURE = 'FAILURE'
}
