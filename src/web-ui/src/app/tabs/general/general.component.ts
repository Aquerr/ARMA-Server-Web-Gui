import { Component, OnInit } from '@angular/core';
import {MaskService} from "../../service/mask.service";
import {ServerGeneralService} from "../../service/server-general.service";

@Component({
  selector: 'app-general',
  templateUrl: './general.component.html',
  styleUrls: ['./general.component.css']
})
export class GeneralComponent implements OnInit {

  serverDirectory: string = "";

  constructor(private maskService: MaskService,
              private serverGeneralService: ServerGeneralService) { }

  ngOnInit(): void {
    this.maskService.show();
    this.serverGeneralService.getServerDirectory().subscribe(response => {
      this.serverDirectory = response.path;
      this.maskService.hide();
    });
  }

  save() {
    this.maskService.show();
    this.serverGeneralService.saveServerDirectory(this.serverDirectory).subscribe(response => {
      this.maskService.hide();
    });
  }
}
