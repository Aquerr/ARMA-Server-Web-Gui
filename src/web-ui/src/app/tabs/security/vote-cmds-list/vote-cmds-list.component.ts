import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { VoteCmd } from "../../../model/vote-cmd.model";
import { CommandListItem } from "./vote-cmd-list-item/vote-cmd-list-item.model";
import { FormGroup } from "@angular/forms";

@Component({
  selector: "app-vote-cmds-list",
  templateUrl: "./vote-cmds-list.component.html",
  styleUrl: "./vote-cmds-list.component.scss",
  standalone: false
})
export class VoteCmdsListComponent implements OnInit {
  // @Input() voteCommands: VoteCmd[] = [];

  @Input() formGroup: FormGroup | undefined;

  @Output() listChanged: EventEmitter<VoteCmd[]> = new EventEmitter<VoteCmd[]>();

  voteCommandsItems: CommandListItem[] = [];

  ngOnInit(): void {
    this.formGroup?.get("allowedVoteCmds")?.valueChanges.subscribe((event: VoteCmd[]) => {
      this.voteCommandsItems = event.map((cmd) => new CommandListItem(cmd));
    });
  }

  deleteItem(item: CommandListItem) {
    this.voteCommandsItems = this.voteCommandsItems.filter((value, index) => {
      return value.command.name != item.command.name;
    });

    this.formGroup
      ?.get("allowedVoteCmds")
      ?.setValue(this.voteCommandsItems.map((item) => item.command));
  }

  addNewCommand() {
    let item = new CommandListItem({} as VoteCmd);
    this.voteCommandsItems.push(item);
  }
}
