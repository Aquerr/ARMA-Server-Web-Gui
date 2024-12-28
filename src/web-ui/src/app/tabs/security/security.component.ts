import {Component, OnInit} from '@angular/core';
import {AllowedFilePatching, ServerSecurityService} from "../../service/server-security.service";
import {MaskService} from "../../service/mask.service";
import {NotificationService} from "../../service/notification.service";
import {SecurityFormService} from './security-form.service';
import {FormGroup} from '@angular/forms';
import {VoteCmd} from "../../model/vote-cmd.model";

@Component({
  selector: 'app-security',
  templateUrl: './security.component.html',
  styleUrls: ['./security.component.scss']
})
export class SecurityComponent implements OnInit {
  public form: FormGroup;

  protected readonly AllowedFilePatching = AllowedFilePatching;

  constructor(private readonly serverSecurityService: ServerSecurityService,
              private readonly maskService: MaskService,
              private readonly notificationService: NotificationService,
              public formService: SecurityFormService) {
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

  setVoteCmds($event: VoteCmd[]) {
    this.form.get('allowedVoteCmds')?.setValue($event);
  }
}
