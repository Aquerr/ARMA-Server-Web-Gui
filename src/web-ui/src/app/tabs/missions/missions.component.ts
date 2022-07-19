import {Component, OnInit, ViewChild} from '@angular/core';
import {UploadMissionComponent} from "./upload-mission/upload-mission.component";
import {Subject} from "rxjs";

@Component({
  selector: 'app-missions',
  templateUrl: './missions.component.html',
  styleUrls: ['./missions.component.css']
})
export class MissionsComponent implements OnInit {

  @ViewChild('uploadMission') uploadMissionComponent!: UploadMissionComponent;

  reloadMissionsDataSubject: Subject<any>

  constructor() {
    this.reloadMissionsDataSubject = new Subject();
  }

  ngOnInit(): void {}

  onFileDropped(file: File) {
    console.log('GOT FILE! ' + file);
    this.uploadMissionComponent.uploadFile(file);
  }

  onMissionUploaded() {
    this.reloadMissionsDataSubject.next(null);
  }
}
