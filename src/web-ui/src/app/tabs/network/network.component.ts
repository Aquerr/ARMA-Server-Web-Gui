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

  upnp: boolean = false;
  maxPing: number = 500;
  loopback: boolean = false;
  disconnectTimeout: number = 5;
  maxDesync: number = 150;
  maxPacketLoss: number = 150;
  enablePlayerDiag: boolean = false;
  steamProtocolMaxDataSize: number = 1024;
  
  constructor(private maskService: MaskService,
              private notificationService: NotificationService,
              private serverNetworkService: ServerNetworkService) { }

  ngOnInit(): void {
    this.maskService.show();
    this.serverNetworkService.getServerNetworkProperties().subscribe(response => {
      this.upnp = response.upnp;
      this.maxPing = response.maxPing;
      this.loopback = response.loopback;
      this.disconnectTimeout = response.disconnectTimeout;
      this.maxDesync = response.maxDesync;
      this.maxPacketLoss = response.maxPacketLoss;
      this.enablePlayerDiag = response.enablePlayerDiag;
      this.steamProtocolMaxDataSize = response.steamProtocolMaxDataSize;
      this.maskService.hide();
    });
  }

  save() {
     this.maskService.show();

    const saveNetworkProperties = {
      upnp: this.upnp,
      maxPing: this.maxPing,
      loopback: this.loopback,
      disconnectTimeout: this.disconnectTimeout,
      maxDesync: this.maxDesync,
      maxPacketLoss: this.maxPacketLoss,
      enablePlayerDiag: this.enablePlayerDiag,
      steamProtocolMaxDataSize: this.steamProtocolMaxDataSize
    } as SaveServerNetworkProperties;

    this.serverNetworkService.saveServerNetworkProperties(saveNetworkProperties).subscribe(response => {
      this.maskService.hide();
      this.notificationService.successNotification('Network settings have been updated!', 'Success');
    });
  }
}
