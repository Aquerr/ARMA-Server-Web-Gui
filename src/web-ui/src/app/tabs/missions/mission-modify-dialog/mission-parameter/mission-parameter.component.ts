import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-mission-parameter',
  templateUrl: './mission-parameter.component.html',
  styleUrls: ['./mission-parameter.component.css']
})
export class MissionParameterComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {

  }

  deleteParameter(event: any) {
    console.log("delete parameter...");
  }
}
