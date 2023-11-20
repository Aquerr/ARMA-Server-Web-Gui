import {Injectable} from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {GetServerNetworkProperties, SaveServerNetworkProperties} from '../../service/server-network.service';

@Injectable({
  providedIn: 'root'
})
export class NetworkFormService {
  constructor(private fb: FormBuilder) {
  }

  getForm(): FormGroup {
    return this.fb.group({
      upnp: [false, [Validators.required]],
      maxPing: [500, [Validators.required]],
      loopback: [false, [Validators.required]],
      disconnectTimeout: [5, [Validators.required, Validators.min(5)]],
      maxDesync: [150, [Validators.required]],
      maxPacketLoss: [150, [Validators.required]],
      enablePlayerDiag: [false, [Validators.required]],
      steamProtocolMaxDataSize: [1024, [Validators.required]]
    });
  }

  setForm(form: FormGroup, data: GetServerNetworkProperties) {
    this.getUpnpControl(form).setValue(data.upnp);
    this.getMaxPingControl(form).setValue(data.maxPing);
    this.getLoopbackControl(form).setValue(data.loopback);
    this.getDisconnectTimeoutControl(form).setValue(data.disconnectTimeout);
    this.getMaxDesyncControl(form).setValue(data.maxDesync);
    this.getMaxPacketLossControl(form).setValue(data.maxPacketLoss);
    this.getEnablePlayerDiagControl(form).setValue(data.enablePlayerDiag);
    this.getSteamProtocolMaxDataSizeControl(form).setValue(data.steamProtocolMaxDataSize);
  }

  get(form: FormGroup) {
    return {
      upnp: this.getUpnpControl(form).value,
      maxPing: this.getMaxPingControl(form).value,
      loopback: this.getLoopbackControl(form).value,
      disconnectTimeout: this.getDisconnectTimeoutControl(form).value,
      maxDesync: this.getMaxDesyncControl(form).value,
      maxPacketLoss: this.getMaxPacketLossControl(form).value,
      enablePlayerDiag: this.getEnablePlayerDiagControl(form).value,
      steamProtocolMaxDataSize: this.getSteamProtocolMaxDataSizeControl(form).value
    } as SaveServerNetworkProperties;
  }

  getUpnpControl(form: FormGroup) {
    return form.get('upnp') as AbstractControl;
  }

  getMaxPingControl(form: FormGroup) {
    return form.get('maxPing') as AbstractControl;
  }

  getLoopbackControl(form: FormGroup) {
    return form.get('loopback') as AbstractControl;
  }

  getDisconnectTimeoutControl(form: FormGroup) {
    return form.get('disconnectTimeout') as AbstractControl;
  }

  getMaxDesyncControl(form: FormGroup) {
    return form.get('maxDesync') as AbstractControl;
  }

  getMaxPacketLossControl(form: FormGroup) {
    return form.get('maxPacketLoss') as AbstractControl;
  }

  getEnablePlayerDiagControl(form: FormGroup) {
    return form.get('enablePlayerDiag') as AbstractControl;
  }

  getSteamProtocolMaxDataSizeControl(form: FormGroup) {
    return form.get('steamProtocolMaxDataSize') as AbstractControl;
  }
}
