import {Component} from '@angular/core';
import {WorkshopMod} from '../../model/workshop.model';
import {WorkshopService} from '../../service/workshop.service';
import {FormControl} from '@angular/forms';

@Component({
  selector: 'app-workshop',
  templateUrl: './workshop.component.html',
  styleUrls: ['./workshop.component.css']
})
export class WorkshopComponent {
  workshopMods: WorkshopMod[] = [];
  subscribedWorkshopMods: WorkshopMod[] = [];
  nextCursor: string = "";
  searchBoxControl!: FormControl;

  private lastSearchText: string = "";

  constructor(private workshopService: WorkshopService) {
    this.searchBoxControl = new FormControl<string>('');
  }

  onSearchBoxKeyDown($event: KeyboardEvent) {
    if ($event.code === 'Enter') {
      this.searchWorkshop('', this.searchBoxControl.value)
    }
  }

  nextPage() {
    this.searchWorkshop(this.nextCursor, this.lastSearchText);
  }

  searchWorkshop(cursor: string, searchText: string) {
    this.lastSearchText = searchText;
    this.workshopService.queryWorkshop({cursor: cursor, searchText: this.lastSearchText}).subscribe(response => {
      this.nextCursor = response.nextCursor;
      this.workshopMods = response.mods;
    });
  }
}
