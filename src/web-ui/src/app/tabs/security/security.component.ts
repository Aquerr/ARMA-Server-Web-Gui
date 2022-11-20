import { Component, OnInit } from '@angular/core';
import {SaveServerSecurityRequest, ServerSecurityService} from "../../service/server-security.service";
import {MaskService} from "../../service/mask.service";
import {NotificationService} from "../../service/notification.service";

@Component({
  selector: 'app-security',
  templateUrl: './security.component.html',
  styleUrls: ['./security.component.css']
})
export class SecurityComponent implements OnInit {
  serverPassword: string = "";
  serverAdminPassword: string = "";
  serverCommandPassword: string = "";
  battleEye: boolean = true;
  verifySignatures: boolean = true;

  constructor(private serverSecurityService: ServerSecurityService,
              private maskService: MaskService,
              private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.maskService.show();
    this.serverSecurityService.getServerSecurity().subscribe(response => {
      this.serverPassword = response.serverPassword;
      this.serverAdminPassword = response.serverAdminPassword;
      this.serverCommandPassword = response.serverCommandPassword;
      this.battleEye = response.battleEye;
      this.verifySignatures = response.verifySignatures;
      this.maskService.hide();
    });
  }

  save() {
    this.maskService.show();

    const serverSecurityRequest = {
      serverPassword: this.serverPassword,
      serverAdminPassword: this.serverAdminPassword,
      serverCommandPassword: this.serverCommandPassword,
      battleEye: this.battleEye,
      verifySignatures: this.verifySignatures
    } as SaveServerSecurityRequest;

    this.serverSecurityService.saveServerSecurity(serverSecurityRequest).subscribe(response => {
      this.maskService.hide();
      this.notificationService.successNotification("Server security updated!");
    });
  }
}
