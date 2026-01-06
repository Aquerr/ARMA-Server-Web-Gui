import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output
} from "@angular/core";
import { CdkDrag, CdkDragDrop, CdkDropList } from "@angular/cdk/drag-drop";
import { MatButton } from "@angular/material/button";
import { Mod } from "../../../model/mod.model";
import { ModListItemComponent } from "../mod-list-item/mod-list-item.component";
import { MatIcon } from "@angular/material/icon";
import { MatFormField, MatOption, MatSelect } from "@angular/material/select";
import { MatLabel } from "@angular/material/form-field";

export type SortBy = "Name_Asc" | "Name_Desc" | "Size_Asc" | "Size_Desc";

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
  @Output() modItemDragDrop = new EventEmitter<CdkDragDrop<Mod[], Mod[], Mod>>();
  @Output() modDelete = new EventEmitter<Mod>();

  filteredMods: Mod[] = [];
  sortBy: SortBy = "Name_Asc";

  ngOnInit() {
    this.reload();
  }

  ngOnChanges() {
    this.reload();
  }

  public reload() {
    this.filteredMods = [...this.mods].sort((a, b) => a.name.localeCompare(b.name));
  }

  moveAllMods() {
    this.moveAll.emit();
  }

  onModItemDragDrop(event: CdkDragDrop<Mod[], Mod[], Mod>) {
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
    switch (this.sortBy) {
      case "Name_Asc":
        this.filteredMods.sort((a, b) => a.name.localeCompare(b.name));
        break;
      case "Name_Desc":
        this.filteredMods.sort((a, b) => a.name.localeCompare(b.name)).reverse();
        break;
      case "Size_Asc":
        this.filteredMods.sort((a, b) => a.sizeBytes - b.sizeBytes);
        break;
      case "Size_Desc":
        this.filteredMods.sort((a, b) => a.sizeBytes - b.sizeBytes).reverse();
        break;
    }
  }
}
