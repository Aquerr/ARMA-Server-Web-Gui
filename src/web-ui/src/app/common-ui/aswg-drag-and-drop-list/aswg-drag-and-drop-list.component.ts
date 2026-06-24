import {
  ChangeDetectorRef,
  Component,
  inject,
  input,
  OnChanges,
  OnInit,
  output,
  signal,
  TemplateRef,
  WritableSignal,
  ChangeDetectionStrategy
} from "@angular/core";
import { MatIcon } from "@angular/material/icon";
import { CdkDrag, CdkDragDrop, CdkDropList } from "@angular/cdk/drag-drop";
import { MatButton } from "@angular/material/button";
import { NgTemplateOutlet } from "@angular/common";
import { MatFormField, MatLabel } from "@angular/material/input";
import { MatOption } from "@angular/material/core";
import { MatSelect, MatSelectTrigger } from "@angular/material/select";

export interface SortOption<T> {
  label: string;
  icon?: string;
  value: string;
  sortFunction: (a: T, b: T) => number;
}

@Component({
  selector: "app-aswg-drag-and-drop-list",
  templateUrl: "./aswg-drag-and-drop-list.component.html",
  imports: [
    MatIcon,
    CdkDrag,
    CdkDropList,
    MatButton,
    NgTemplateOutlet,
    MatFormField,
    MatLabel,
    MatOption,
    MatSelect,
    MatSelectTrigger
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: "./aswg-drag-and-drop-list.component.scss"
})
export class AswgDragAndDropListComponent<T> implements OnInit, OnChanges {
  private readonly changeDetectorRef = inject(ChangeDetectorRef);

  public listId = input.required<string>();
  public moveAllButtonName = input.required<string>();
  public moveAllButtonIcon = input.required<string>();
  public sortEnabled = input<boolean>(false);
  public listHeader = input.required<string>();
  public listItemTemplate = input.required<TemplateRef<unknown>>();
  public filterFunction = input<(item: T, searchPhrase: string) => boolean>(() => false);
  public sortingOptions = input<SortOption<T>[]>([]);
  public selectedSortingOption!: SortOption<T> | undefined;

  public items = input<T[]>([]);

  public moveAll = output<unknown>();
  public itemDragDrop = output<CdkDragDrop<T[], T[], T>>();
  public itemDelete = output<T>();

  public filteredItems: WritableSignal<T[]> = signal([]);

  public ngOnInit(): void {
    this.selectedSortingOption = this.sortingOptions().at(0);

    this.reload();
  }

  public ngOnChanges(): void {
    this.reload();
  }

  public moveAllItems(): void {
    this.moveAll.emit({});
  }

  public reload() {
    if (this.sortEnabled()) {
      this.filteredItems.set([...this.items()].sort(this.selectedSortingOption?.sortFunction));
    } else {
      this.filteredItems.set([...this.items()]);
    }
  }

  public onItemDragDrop(event: CdkDragDrop<T[], T[], T>) {
    this.itemDragDrop.emit(event);
  }

  public filterItems(searchPhrase: string) {
    this.filteredItems.set(this.items().filter((item) => this.filterFunction()(item, searchPhrase)));
    this.sortItems();
  }

  public sortItems(): void {
    this.filteredItems.update((oldMods) => oldMods.sort(this.selectedSortingOption?.sortFunction));
    this.changeDetectorRef.markForCheck();
  }
}
