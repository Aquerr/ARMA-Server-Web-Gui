import {Component, inject, OnInit} from '@angular/core';
import {MissionUploadService} from "../service/mission-upload.service";
import {MatSnackBarRef} from "@angular/material/snack-bar";

@Component({
  selector: 'app-mission-upload-snack-bar',
  templateUrl: './mission-upload-snack-bar.component.html',
  styleUrls: ['./mission-upload-snack-bar.component.css']
})
export class MissionUploadSnackBarComponent implements OnInit {

  snackBarRef = inject(MatSnackBarRef);

  constructor(private missionUploadService: MissionUploadService) { }

  ngOnInit(): void {
  }

  getUploadingMissions() {
    return this.missionUploadService.getUploadingMissions();
  }
}
