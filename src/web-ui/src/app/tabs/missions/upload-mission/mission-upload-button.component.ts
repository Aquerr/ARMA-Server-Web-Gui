import {Component, OnInit} from '@angular/core';
import {MissionUploadService} from "../service/mission-upload.service";
import {BehaviorSubject} from "rxjs";

@Component({
  selector: 'app-upload-mission',
  templateUrl: './mission-upload-button.component.html',
  styleUrls: ['./mission-upload-button.component.scss']
})
export class MissionUploadButtonComponent implements OnInit {

  missionUploadedSubject!: BehaviorSubject<any>;

  constructor(private missionUploadService: MissionUploadService) {
    this.missionUploadedSubject = new BehaviorSubject<boolean>(false);
  }

  ngOnInit(): void {
  }

  onFileSelected(event: Event) {
    console.log(event);

    const target = (event.target as HTMLInputElement);

    if (!target.files)
      return;

    const file : File = target.files[0];
    if (file)
    {
      this.missionUploadService.uploadMission(file);
    }
  }
}
