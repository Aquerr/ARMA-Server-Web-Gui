import { VoteCmd } from "../../../../model/vote-cmd.model";

export class CommandListItem {
  command: VoteCmd;
  editing: boolean = false;

  constructor(command: VoteCmd, editing = false) {
    this.command = command;
    this.editing = editing;
  }
}
