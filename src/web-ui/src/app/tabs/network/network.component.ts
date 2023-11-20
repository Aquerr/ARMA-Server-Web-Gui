import {Component, OnInit} from '@angular/core';
import {ServerNetworkService} from '../../service/server-network.service';
import {MaskService} from '../../service/mask.service';
import {NotificationService} from '../../service/notification.service';
import {NetworkFormService} from './network-form.service';
import {FormGroup} from '@angular/forms';

@Component({
  selector: 'app-network',
  templateUrl: './network.component.html',
  styleUrls: ['./network.component.css']
})
export class NetworkComponent implements OnInit {
  form: FormGroup;
  upnp: boolean = false;
  maxPing: number = 500;
  loopback: boolean = false;
  disconnectTimeout: number = 5;
  maxDesync: number = 150;
  maxPacketLoss: number = 150;
  enablePlayerDiag: boolean = false;
  steamProtocolMaxDataSize: number = 1024;

  constructor(private maskService: MaskService,
              private notificationService: NotificationService,
              private serverNetworkService: ServerNetworkService,
              private formService: NetworkFormService) {
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
}
