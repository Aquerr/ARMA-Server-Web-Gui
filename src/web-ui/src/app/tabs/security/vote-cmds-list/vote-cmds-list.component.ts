import { Component, Input } from "@angular/core";
import { VoteCmd } from "../../../model/vote-cmd.model";
import { CommandListItem } from "./vote-cmd-list-item/vote-cmd-list-item.model";
import { FormControl } from "@angular/forms";
import { MatIconButton } from "@angular/material/button";
import { VoteCmdListItemComponent } from "./vote-cmd-list-item/vote-cmd-list-item.component";
import { MatIcon } from "@angular/material/icon";

@Component({
  selector: "app-vote-cmds-list",
  templateUrl: "./vote-cmds-list.component.html",
  imports: [
    MatIconButton,
    VoteCmdListItemComponent,
    MatIcon
  ],
  styleUrl: "./vote-cmds-list.component.scss"
})
export class VoteCmdsListComponent {
  @Input() control: FormControl<CommandListItem[]> | undefined;

  deleteItem(index: number) {
    this.control?.patchValue(this.control?.value.filter((value, itemIndex) => {
      return index != itemIndex;
    }));
  }

  addNewCommand() {
    const item = new CommandListItem({} as VoteCmd, true);
    this.control?.patchValue([...this.control?.value, item]);
  }
}
