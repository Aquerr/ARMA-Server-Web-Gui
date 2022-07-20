import { Component, OnInit } from '@angular/core';
import {ServerSecurityService} from "../../service/server-security.service";
import {MaskService} from "../../service/mask.service";

@Component({
  selector: 'app-security',
  templateUrl: './security.component.html',
  styleUrls: ['./security.component.css']
})
export class SecurityComponent implements OnInit {
  serverPassword: string = "";
  serverAdminPassword: string = "";
  serverCommandPassword: string = "";

  constructor(private serverSecurityService: ServerSecurityService,
              private maskService: MaskService) { }

  ngOnInit(): void {
    this.maskService.show();
    this.serverSecurityService.getServerSecurity().subscribe(response => {
      this.serverPassword = response.serverPassword;
      this.serverAdminPassword = response.serverAdminPassword;
      this.serverCommandPassword = response.serverCommandPassword;
      this.maskService.hide();
    });
  }

  save() {
    console.log("Saving!");
  }
}
