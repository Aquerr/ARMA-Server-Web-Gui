import { VoteCmd } from "../../../../model/vote-cmd.model";

export class CommandListItem {
  command: VoteCmd;
  editing: boolean = false;

  constructor(command: VoteCmd) {
    this.command = command;
  }
}
