import {Component, EventEmitter, Input, Output} from '@angular/core';
import {CommandListItem} from "./vote-cmd-list-item.model";

@Component({
    selector: 'app-vote-cmd-list-item',
    templateUrl: './vote-cmd-list-item.component.html',
    styleUrl: './vote-cmd-list-item.component.scss',
    standalone: false
})
export class VoteCmdListItemComponent {
  @Input() item!: CommandListItem;

  @Output() onDelete: EventEmitter<any> = new EventEmitter<any>();

  deleteClick() {
    this.onDelete.emit();
  }

  onItemUpdate() {

  }

  doubleClick() {
    this.item.editing = !this.item.editing;
  }

  protected readonly onkeydown = onkeydown;

  onEnter() {
    if (this.item.editing) {
      this.item.editing = !this.item.editing;
    }
  }
}
