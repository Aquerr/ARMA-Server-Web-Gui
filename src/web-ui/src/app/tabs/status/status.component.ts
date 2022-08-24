import { Component, OnInit } from '@angular/core';
import {MaskService} from "../../service/mask.service";
import {ServerStatusService} from "../../service/server-status.service";

@Component({
  selector: 'app-status',
  templateUrl: './status.component.html',
  styleUrls: ['./status.component.css']
})
export class StatusComponent implements OnInit {

  serverStatus: string = 'OFFLINE';
  playerList: string[] = [];

  constructor(private maskService: MaskService,
              private serverStatusService: ServerStatusService) { }

  ngOnInit(): void {
    this.maskService.show();
    this.serverStatusService.getStatus().subscribe(response => {
      this.serverStatus = response.status;
      this.playerList = response.playerList;
      this.maskService.hide();
    });
  }

  getServerStatus() {
    return this.serverStatus;
  }
}
