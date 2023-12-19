import {Component, OnInit} from '@angular/core';
import {AllowedFilePatching, ServerSecurityService} from "../../service/server-security.service";
import {MaskService} from "../../service/mask.service";
import {NotificationService} from "../../service/notification.service";
import {SecurityFormService} from './security-form.service';
import {FormGroup} from '@angular/forms';
import {MatChipEditedEvent, MatChipInputEvent} from "@angular/material/chips";
import {COMMA, ENTER} from "@angular/cdk/keycodes";

@Component({
  selector: 'app-security',
  templateUrl: './security.component.html',
  styleUrls: ['./security.component.css']
})
export class SecurityComponent implements OnInit {
  public form: FormGroup;

  constructor(private serverSecurityService: ServerSecurityService,
              private maskService: MaskService,
              private notificationService: NotificationService,
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

  addNewAllowedFileExtension(event: MatChipInputEvent) {
    const value = (event.value || '').trim();

    if (value) {
      this.formService.getAllowedLoadFileExtensions(this.form).value.push(value);
    }
    this.formService.getAllowedLoadFileExtensions(this.form).updateValueAndValidity();

    event.chipInput.clear();
  }

  editAllowedFileExtension(fileExtension: string, event: MatChipEditedEvent) {
    const value = event.value.trim();

    if (!value) {
      this.removeAllowedFileExtension(value);
      return;
    }

    const index = this.formService.getAllowedLoadFileExtensions(this.form).value.indexOf(fileExtension);
    if (index >= 0) {
      this.formService.getAllowedLoadFileExtensions(this.form).value[index] = value;
    }
  }

  removeAllowedFileExtension(fileExtension: string) {
    const index = this.formService.getAllowedLoadFileExtensions(this.form).value.indexOf(fileExtension);
    if (index >= 0) {
      this.formService.getAllowedLoadFileExtensions(this.form).value.splice(index, 1);
      this.formService.getAllowedLoadFileExtensions(this.form).updateValueAndValidity();
    }
  }

  protected readonly ENTER = ENTER;
  protected readonly COMMA = COMMA;

  editAdminUUID(adminUUID: string, event: MatChipEditedEvent) {
    const value = event.value.trim();

    if (!value) {
      this.removeAdminUUID(value);
      return;
    }

    const index = this.formService.getAdminUUIDs(this.form).value.indexOf(adminUUID);
    if (index >= 0) {
      this.formService.getAdminUUIDs(this.form).value[index] = value;
    }
  }

  removeAdminUUID(adminUUID: string) {
    const index = this.formService.getAdminUUIDs(this.form).value.indexOf(adminUUID);
    if (index >= 0) {
      this.formService.getAdminUUIDs(this.form).value.splice(index, 1);
      this.formService.getAdminUUIDs(this.form).updateValueAndValidity();
    }
  }

  addNewAdminUUID(event: MatChipInputEvent) {
    const value = (event.value || '').trim();

    if (value) {
      this.formService.getAdminUUIDs(this.form).value.push(value);
    }
    this.formService.getAdminUUIDs(this.form).updateValueAndValidity();

    event.chipInput.clear();
  }

  protected readonly AllowedFilePatching = AllowedFilePatching;
}
