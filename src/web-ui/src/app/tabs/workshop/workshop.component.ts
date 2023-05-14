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
  nextCursor: string = "";
  searchBoxControl!: FormControl;

  constructor(private workshopService: WorkshopService) {
    this.searchBoxControl = new FormControl<string>('');
  }

  searchWorkshop(cursor: string) {
    this.workshopService.queryWorkshop({cursor: cursor, searchText: this.searchBoxControl.value}).subscribe(response => {
      this.nextCursor = response.nextCursor;
      this.workshopMods = response.mods;
    });
  }

  nextPage() {
    this.searchWorkshop(this.nextCursor);
  }
}
