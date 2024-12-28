import {Component, OnInit} from '@angular/core';
import {ServerNetworkService} from '../../service/server-network.service';
import {MaskService} from '../../service/mask.service';
import {NotificationService} from '../../service/notification.service';
import {NetworkFormService} from './network-form.service';
import {FormGroup} from '@angular/forms';

@Component({
  selector: 'app-network',
  templateUrl: './network.component.html',
  styleUrls: ['./network.component.scss']
})
export class NetworkComponent implements OnInit {
  form: FormGroup;

  constructor(private readonly maskService: MaskService,
              private readonly notificationService: NotificationService,
              private readonly serverNetworkService: ServerNetworkService,
              private readonly formService: NetworkFormService) {
    this.form = this.formService.getForm();
  }

  ngOnInit(): void {
    this.maskService.show();
    this.serverNetworkService.getServerNetworkProperties().subscribe(response => {
      this.formService.setForm(this.form, response);
      this.maskService.hide();
    });
  }

  save() {
    this.form.markAllAsTouched();
    if (this.form.valid) {
      this.maskService.show();
      const request = this.formService.get(this.form);
      this.serverNetworkService.saveServerNetworkProperties(request).subscribe({
        next: () => {
          this.maskService.hide();
          this.notificationService.successNotification('Network settings have been updated!', 'Success');
        },
        error: () => {
          this.maskService.hide();
          this.notificationService.errorNotification('Network settings have not been updated!', 'Error');
        }
      });
    }
  }

  hasFormError(controlName: string, errorName: string): boolean {
    return this.form.get(controlName)?.hasError(errorName)!;
  }

  allowDecimals(event: KeyboardEvent) {
    return this.allowDigits(event) || event.key === ".";
  }

  allowDigits(event: KeyboardEvent) {
    const number = event.key as unknown as number;
    return number >= 0 && number <= 9 || this.isInputAllowedKey(event.key);
  }

  isInputAllowedKey(key: string) {
    return key === 'Backspace' || key === 'ArrowLeft' || key === 'ArrowRight' || key === 'Tab' || key === 'Delete';
  }
}
