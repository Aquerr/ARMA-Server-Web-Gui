import { Component, OnInit } from '@angular/core';
import {MaskService} from "../../service/mask.service";

@Component({
  selector: 'app-general',
  templateUrl: './general.component.html',
  styleUrls: ['./general.component.css']
})
export class GeneralComponent implements OnInit {

  constructor(private maskService: MaskService) { }

  ngOnInit(): void {
    this.maskService.show();
    setTimeout(() => {this.maskService.hide()}, 4000)
  }

}
