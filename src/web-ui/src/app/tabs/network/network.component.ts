import { Component, OnInit } from '@angular/core';
import {SaveServerNetworkProperties, ServerNetworkService} from '../../service/server-network.service';
import {MaskService} from '../../service/mask.service';
import {NotificationService} from '../../service/notification.service';

@Component({
  selector: 'app-network',
  templateUrl: './network.component.html',
  styleUrls: ['./network.component.css']
})
export class NetworkComponent implements OnInit {

  maxPing: number = 500;

  constructor(private maskService: MaskService,
              private notificationService: NotificationService,
              private serverNetworkService: ServerNetworkService) { }

  ngOnInit(): void {
  }

  save() {
     this.maskService.show();

    const saveNetworkProperties = {
      maxPing: this.maxPing
    } as SaveServerNetworkProperties;

    this.serverNetworkService.saveServerNetworkProperties(saveNetworkProperties).subscribe(response => {
      this.maskService.hide();
      this.notificationService.successNotification('Network settings have been updated!', 'Success');
    });
  }
}
