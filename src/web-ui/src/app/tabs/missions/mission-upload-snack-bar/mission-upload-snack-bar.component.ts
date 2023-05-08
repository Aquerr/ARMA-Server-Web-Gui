import {Component, inject, OnInit} from '@angular/core';
import {MatLegacySnackBarRef as MatSnackBarRef} from "@angular/material/legacy-snack-bar";
import {MissionUploadService} from "../service/mission-upload.service";

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
