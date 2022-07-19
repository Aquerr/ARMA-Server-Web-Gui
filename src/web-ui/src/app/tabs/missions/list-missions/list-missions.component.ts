import {Component, ElementRef, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ServerMissionsService} from "../../../service/server-missions.service";
import {MatSelectionListChange} from "@angular/material/list";
import {MatTableDataSource} from "@angular/material/table";
import {SelectionModel} from "@angular/cdk/collections";
import {Subject, Subscription} from "rxjs";
import {MaskService} from "../../../service/mask.service";

@Component({
  selector: 'app-list-missions',
  templateUrl: './list-missions.component.html',
  styleUrls: ['./list-missions.component.css']
})
export class ListMissionsComponent implements OnInit, OnDestroy {

  @ViewChild('missionsList') missionsListElement!: ElementRef;

  missions: string[] = [];

  displayedColumns: string[] = ['select', 'name', 'delete'];
  missionsDataSource = new MatTableDataSource(this.missions);
  selection = new SelectionModel<string>(true, []);

  @Input() reloadMissionsDataSubject!: Subject<any>;
  reloadMissionDataSubscription!: Subscription;

  constructor(private missionsService: ServerMissionsService,
              private maskService: MaskService) {
  }

  ngOnInit(): void {
    this.reloadMissions();
    this.reloadMissionDataSubscription = this.reloadMissionsDataSubject.subscribe(() => {
      this.reloadMissions();
    });
  }

  ngOnDestroy(): void {
    this.reloadMissionDataSubscription.unsubscribe();
  }

  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.missionsDataSource.data.length;
    return numSelected === numRows;
  }

  toggleAllRows() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }

    this.selection.select(...this.missionsDataSource.data);
  }


  checkboxLabel(row?: string) {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row + 1}`
  }









  onSelectionChange(event: MatSelectionListChange) {
    // console.log(event);
  }

  onMissionClick($event: any) {
    // console.log($event);
  }

  deleteMission($event: MouseEvent) {

  }

  selectMission($event: MouseEvent, missionName: string) {
    if (this.selection.isSelected(missionName)) {
      this.selection.deselect(missionName);
    } else {
      this.selection.select(missionName);
    }
  }

  private reloadMissions(): void {
    this.maskService.show();
    this.missionsService.getInstalledMissions().subscribe(response => {
      this.missions = response.missions;
      this.missionsDataSource = new MatTableDataSource(this.missions);
      this.maskService.hide();
    });
  }

  onMissionDelete(missionName: string) {
    this.maskService.show();
    this.missionsService.deleteMission(missionName).subscribe(response => {
      this.maskService.hide();
      this.reloadMissionsDataSubject.next(null);
    });
  }
}
