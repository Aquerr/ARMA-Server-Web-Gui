import { Component, EventEmitter, Input, Output } from "@angular/core";
import { CommandListItem } from "./vote-cmd-list-item.model";
import { MatCard, MatCardContent } from "@angular/material/card";
import { MatTooltip } from "@angular/material/tooltip";
import { MatFormField, MatInput, MatLabel } from "@angular/material/input";
import { FormsModule } from "@angular/forms";
import { MatOption, MatSelect } from "@angular/material/select";
import { MatIcon } from "@angular/material/icon";
import { MatIconButton } from "@angular/material/button";

@Component({
  selector: "app-vote-cmd-list-item",
  templateUrl: "./vote-cmd-list-item.component.html",
  imports: [
    MatCard,
    MatCardContent,
    MatTooltip,
    MatFormField,
    MatLabel,
    MatInput,
    FormsModule,
    MatSelect,
    MatOption,
    MatIcon,
    MatIconButton
  ],
  styleUrl: "./vote-cmd-list-item.component.scss"
})
export class VoteCmdListItemComponent {
  @Input() item!: CommandListItem;

  @Output() deleted: EventEmitter<void> = new EventEmitter<void>();

  deleteClick() {
    this.deleted.emit();
  }

  doubleClick() {
    this.item.editing = !this.item.editing;
  }

  onEnter() {
    if (this.item.editing) {
      this.item.editing = !this.item.editing;
    }
  }
}
