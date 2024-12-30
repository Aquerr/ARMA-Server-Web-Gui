import { Component, OnInit } from '@angular/core';
import {MaskService} from "../../service/mask.service";
import {NotificationService} from "../../service/notification.service";
import {ServerLoggingService} from "../../service/server-logging.service";

@Component({
    selector: 'app-logging',
    templateUrl: './logging.component.html',
    styleUrls: ['./logging.component.scss'],
    standalone: false
})
export class LoggingComponent implements OnInit {

  logFile: string = '';

  constructor(
              private serverLoggingService: ServerLoggingService,
              private maskService: MaskService,
              private notificationService: NotificationService) {

  }

  ngOnInit(): void {
    this.maskService.show();
    this.serverLoggingService.getLoggingSectionData().subscribe(response => {
      this.logFile = response.logFile;
      this.maskService.hide();
    });
  }

  save() {
    const loggingSectionData = {
      logFile: this.logFile
    };

    this.maskService.show();
    this.serverLoggingService.saveLoggingSectionData(loggingSectionData).subscribe(response => {
      this.maskService.hide();
      this.notificationService.successNotification('Log file has been updated!');
    });
  }
}
