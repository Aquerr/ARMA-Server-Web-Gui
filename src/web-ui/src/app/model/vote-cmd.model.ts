export interface VoteCmd {
  name: string;
  allowedPreMission: boolean;
  allowedPostMission: boolean;
  votingThreshold: number;
  percentageSideVotingThreshold: number;
}
