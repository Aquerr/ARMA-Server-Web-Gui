import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges
} from "@angular/core";
import { CdkDrag, CdkDragDrop, CdkDropList } from "@angular/cdk/drag-drop";
import { MatButton } from "@angular/material/button";
import { Mod } from "../../../model/mod.model";
import { ModListItemComponent } from "../mod-list-item/mod-list-item.component";
import { MatIcon } from "@angular/material/icon";
import { MatFormField, MatOption, MatSelect } from "@angular/material/select";
import { MatLabel } from "@angular/material/form-field";

export type SortBy = "Name" | "Size";

@Component({
  selector: "app-mod-list",
  imports: [
    CdkDrag,
    MatButton,
    CdkDropList,
    ModListItemComponent,
    MatIcon,
    MatSelect,
    MatOption,
    MatFormField,
    MatLabel
  ],
  templateUrl: "./mod-list.component.html",
  styleUrl: "./mod-list.component.scss"
})
export class ModListComponent implements OnInit, OnChanges {
  @Input() listHeader!: string;
  @Input() moveAllModsButtonName!: string;
  @Input() moveAllModsButtonIcon!: string;

  @Input() mods!: Mod[];
  @Output() moveAll = new EventEmitter<unknown>();
  @Output() modItemDragDrop = new EventEmitter<CdkDragDrop<Mod[], any>>();
  @Output() modDelete = new EventEmitter<Mod>();

  filteredMods: Mod[] = [];
  sortBy: SortBy = "Name";

  ngOnInit() {
    this.reload();
  }

  ngOnChanges(changes: SimpleChanges) {
    this.reload();
  }

  public reload() {
    this.filteredMods = [...this.mods].sort((a, b) => a.name.localeCompare(b.name));
  }

  moveAllMods() {
    this.moveAll.emit();
  }

  onModItemDragDrop(event: CdkDragDrop<Mod[], any>) {
    this.modItemDragDrop.emit(event);
  }

  onModDelete(mod: Mod) {
    this.modDelete.emit(mod);
  }

  filterMods(searchPhrase: string) {
    this.filteredMods = this.mods.filter((mod) =>
      mod.name.toLowerCase().includes(searchPhrase.toLowerCase())
    );
    this.sortModList();
  }

  public sortModList() {
    if (this.sortBy == "Name") {
      this.filteredMods.sort((a, b) => a.name.localeCompare(b.name));
    } else if (this.sortBy == "Size") {
      this.filteredMods.sort((a, b) => a.sizeBytes - b.sizeBytes);
    }
  }
}
