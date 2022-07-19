import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {ServerMissionsService} from "../../../service/server-missions.service";
import {MatSelectionListChange} from "@angular/material/list";

@Component({
  selector: 'app-list-missions',
  templateUrl: './list-missions.component.html',
  styleUrls: ['./list-missions.component.css']
})
export class ListMissionsComponent implements OnInit {

  @ViewChild('missionsList') missionsListElement!: ElementRef;

  missions: string[] = [];

  constructor(private missionsService: ServerMissionsService) { }

  ngOnInit(): void {
    this.missionsService.getInstalledMissions().subscribe(response => {
      this.missions = response.missions;
    });
  }

  onSelectionChange(event: MatSelectionListChange) {
    // console.log(event);
  }

  onMissionClick($event: any) {
    // console.log($event);
  }
}
