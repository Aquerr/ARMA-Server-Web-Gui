import { Component, OnInit } from '@angular/core';
import {MaskService} from "../../service/mask.service";
import {ServerGeneralService} from "../../service/server-general.service";

@Component({
  selector: 'app-general',
  templateUrl: './general.component.html',
  styleUrls: ['./general.component.css']
})
export class GeneralComponent implements OnInit {

  constructor(private maskService: MaskService,
              private serverGeneralService: ServerGeneralService) { }

  ngOnInit(): void {
    this.maskService.show();
    setTimeout(() => {this.maskService.hide()}, 4000);


    this.serverGeneralService.home();
  }
}
