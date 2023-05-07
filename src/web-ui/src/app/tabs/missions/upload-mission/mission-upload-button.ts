import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {MissionUploadService} from "../service/mission-upload.service";

@Component({
  selector: 'app-upload-mission',
  templateUrl: './mission-upload-button.html',
  styleUrls: ['./mission-upload-button.css']
})
export class MissionUploadButton implements OnInit {

  @Output() missionUploaded: EventEmitter<any> = new EventEmitter<any>();

  constructor(private missionUploadService: MissionUploadService) { }

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
