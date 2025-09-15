export interface JobSettings {
  enabled: string;
  cron: string;
  parameters: [{name: string, description: string, value: string}];
  lastExecutionDate: string;
  nextExecutionDate: string;
}
