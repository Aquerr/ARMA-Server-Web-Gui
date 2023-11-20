import {Component, OnInit} from '@angular/core';
import {SaveServerSecurityRequest, ServerSecurityService} from "../../service/server-security.service";
import {MaskService} from "../../service/mask.service";
import {NotificationService} from "../../service/notification.service";
import {SecurityFormService} from './security-form.service';
import {FormGroup} from '@angular/forms';

@Component({
  selector: 'app-security',
  templateUrl: './security.component.html',
  styleUrls: ['./security.component.css']
})
export class SecurityComponent implements OnInit {
  form: FormGroup;
  serverPassword: string = "";
  serverAdminPassword: string = "";
  serverCommandPassword: string = "";
  battleEye: boolean = true;
  verifySignatures: boolean = true;
  allowedFilePatching: number = 0;

  constructor(private serverSecurityService: ServerSecurityService,
              private maskService: MaskService,
              private notificationService: NotificationService,
              private formService: SecurityFormService) {
    this.form = this.formService.getForm();
  }

  ngOnInit(): void {
    this.maskService.show();
    this.serverSecurityService.getServerSecurity().subscribe(response => {
      this.formService.setForm(this.form, response);
      this.maskService.hide();
    });
  }

  save() {
    this.form.markAllAsTouched();
    if (this.form.valid) {
      this.maskService.show();
      const request = this.formService.get(this.form);
      this.serverSecurityService.saveServerSecurity(request).subscribe({
        next: () => {
          this.maskService.hide();
          this.notificationService.successNotification("Server security updated!");
        },
        error: () => {
          this.maskService.hide();
          this.notificationService.errorNotification("Server security not updated!");
        }
      });
    }
  }

  hasFormError(controlName: string, errorName: string): boolean {
    return this.form.get(controlName)?.hasError(errorName)!;
  }
}
