import {Component, OnInit} from '@angular/core';
import {WorkshopMod} from '../../model/workshop.model';
import {WorkshopService} from '../../service/workshop.service';
import {FormControl} from '@angular/forms';
import {MaskService} from "../../service/mask.service";
import {NgxSpinnerService} from "ngx-spinner";

@Component({
  selector: 'app-workshop',
  templateUrl: './workshop.component.html',
  styleUrls: ['./workshop.component.css']
})
export class WorkshopComponent implements OnInit {
  workshopMods: WorkshopMod[] = [];
  installedWorkshopMods: WorkshopMod[] = [];

  nextCursor: string = "";
  searchBoxControl!: FormControl;
  private lastSearchText: string = "";

  constructor(private workshopService: WorkshopService,
              private ngxSpinnerService: NgxSpinnerService) {
    this.searchBoxControl = new FormControl<string>('');
  }

  ngOnInit(): void {
    this.reloadInstalledModList();
  }

  onSearchBoxKeyDown($event: KeyboardEvent) {
    if ($event.code === 'Enter' || $event.code === 'NumpadEnter') {
      this.searchWorkshop('', this.searchBoxControl.value)
    }
  }
  nextPage() {
    this.searchWorkshop(this.nextCursor, this.lastSearchText);
  }

  searchWorkshop(cursor: string, searchText: string) {
    this.ngxSpinnerService.show("workshop-mods");
    this.lastSearchText = searchText;
    this.workshopService.queryWorkshop({cursor: cursor, searchText: this.lastSearchText}).subscribe(response => {
      this.nextCursor = response.nextCursor;
      this.workshopMods = response.mods;
      this.ngxSpinnerService.hide("workshop-mods");
    });
  }


  canInstall(workshopMod: WorkshopMod) {
    return this.installedWorkshopMods.find(mod => mod.fileId === workshopMod.fileId) === undefined;
  }

  onModInstallDelete($event: any) {
    this.reloadInstalledModList();
  }

  private reloadInstalledModList() {
    this.workshopService.getInstalledWorkshopItems().subscribe(response => {
      this.installedWorkshopMods = response.mods;
    });
  }
}
