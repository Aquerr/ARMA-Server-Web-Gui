import { Injectable } from "@angular/core";
import { AbstractControl, FormBuilder, FormGroup, Validators } from "@angular/forms";
import { ServerNetworkProperties } from "../../service/server-network.service";

@Injectable({
  providedIn: "root"
})
export class NetworkFormService {
  private readonly digitsRegex = /^\d+$/;
  private readonly decimalRegex = /^\d+(.\d+)?$/;

  constructor(private readonly fb: FormBuilder) {}

  getForm(): FormGroup {
    return this.fb.group({
      upnp: [false, [Validators.required]],
      maxPing: [500, [Validators.required, Validators.pattern(this.digitsRegex)]],
      loopback: [false, [Validators.required]],
      disconnectTimeout: [5, [Validators.required, Validators.min(1)]],
      maxDesync: [150, [Validators.required, Validators.pattern(this.digitsRegex)]],
      maxPacketLoss: [150, [Validators.required, Validators.pattern(this.digitsRegex)]],
      enablePlayerDiag: [false, [Validators.required]],
      steamProtocolMaxDataSize: [1024, [Validators.required, Validators.pattern(this.digitsRegex)]],
      minBandwidth: [131072, [Validators.required, Validators.pattern(this.digitsRegex)]],
      maxBandwidth: [10000000000, [Validators.required, Validators.pattern(this.digitsRegex)]],
      maxMsgSend: [128, [Validators.required, Validators.pattern(this.digitsRegex)]],
      maxSizeGuaranteed: [512, [Validators.required, Validators.pattern(this.digitsRegex)]],
      maxSizeNonGuaranteed: [256, [Validators.required, Validators.pattern(this.digitsRegex)]],
      minErrorToSend: ["0.001", [Validators.required, Validators.pattern(this.decimalRegex)]],
      minErrorToSendNear: ["0.01", [Validators.required, Validators.pattern(this.decimalRegex)]],
      maxCustomFileSize: [0, [Validators.required, Validators.pattern(this.digitsRegex)]],
      maxPacketSize: [1400, [Validators.required, Validators.pattern(this.digitsRegex)]],
      manualKickTimeout: [
        60,
        [Validators.required, Validators.min(-2), Validators.pattern(this.digitsRegex)]
      ],
      connectivityKickTimeout: [
        60,
        [Validators.required, Validators.min(-2), Validators.pattern(this.digitsRegex)]
      ],
      battlEyeKickTimeout: [
        60,
        [Validators.required, Validators.min(-2), Validators.pattern(this.digitsRegex)]
      ],
      harmlessKickTimeout: [
        60,
        [Validators.required, Validators.min(-2), Validators.pattern(this.digitsRegex)]
      ]
    });
  }

  setForm(form: FormGroup, data: ServerNetworkProperties) {
    this.getUpnpControl(form).setValue(data.upnp);
    this.getMaxPingControl(form).setValue(data.maxPing);
    this.getLoopbackControl(form).setValue(data.loopback);
    this.getDisconnectTimeoutControl(form).setValue(data.disconnectTimeout);
    this.getMaxDesyncControl(form).setValue(data.maxDesync);
    this.getMaxPacketLossControl(form).setValue(data.maxPacketLoss);
    this.getEnablePlayerDiagControl(form).setValue(data.enablePlayerDiag);
    this.getSteamProtocolMaxDataSizeControl(form).setValue(data.steamProtocolMaxDataSize);
    this.getMinBandwidth(form).setValue(data.minBandwidth);
    this.getMaxBandwidth(form).setValue(data.maxBandwidth);
    this.getMaxMsgSend(form).setValue(data.maxMsgSend);
    this.getMaxSizeGuaranteed(form).setValue(data.maxSizeGuaranteed);
    this.getMaxSizeNonGuaranteed(form).setValue(data.maxSizeNonGuaranteed);
    this.getMinErrorToSend(form).setValue(data.minErrorToSend);
    this.getMinErrorToSendNear(form).setValue(data.minErrorToSendNear);
    this.getMaxCustomFileSize(form).setValue(data.maxCustomFileSize);
    this.getMaxPacketSize(form).setValue(data.maxPacketSize);
    this.getManualKickTimeout(form).setValue(data.kickTimeouts.manualKickTimeoutSeconds);
    this.getConnectivityKickTimeout(form).setValue(
      data.kickTimeouts.connectivityKickTimeoutSeconds
    );
    this.getBattlEyeKickTimeout(form).setValue(data.kickTimeouts.battlEyeKickTimeoutSeconds);
    this.getHarmlessKickTimeout(form).setValue(data.kickTimeouts.harmlessKickTimeoutSeconds);
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
      steamProtocolMaxDataSize: this.getSteamProtocolMaxDataSizeControl(form).value,
      minBandwidth: this.getMinBandwidth(form).value,
      maxBandwidth: this.getMaxBandwidth(form).value,
      maxMsgSend: this.getMaxMsgSend(form).value,
      maxSizeGuaranteed: this.getMaxSizeGuaranteed(form).value,
      maxSizeNonGuaranteed: this.getMaxSizeNonGuaranteed(form).value,
      minErrorToSend: this.getMinErrorToSend(form).value,
      minErrorToSendNear: this.getMinErrorToSendNear(form).value,
      maxCustomFileSize: this.getMaxCustomFileSize(form).value,
      maxPacketSize: this.getMaxPacketSize(form).value,
      kickTimeouts: {
        manualKickTimeoutSeconds: this.getManualKickTimeout(form).value,
        connectivityKickTimeoutSeconds: this.getConnectivityKickTimeout(form).value,
        battlEyeKickTimeoutSeconds: this.getBattlEyeKickTimeout(form).value,
        harmlessKickTimeoutSeconds: this.getHarmlessKickTimeout(form).value
      }
    } as ServerNetworkProperties;
  }

  getUpnpControl(form: FormGroup) {
    return form.get("upnp") as AbstractControl<boolean>;
  }

  getMaxPingControl(form: FormGroup) {
    return form.get("maxPing") as AbstractControl<number>;
  }

  getLoopbackControl(form: FormGroup) {
    return form.get("loopback") as AbstractControl<boolean>;
  }

  getDisconnectTimeoutControl(form: FormGroup) {
    return form.get("disconnectTimeout") as AbstractControl<number>;
  }

  getMaxDesyncControl(form: FormGroup) {
    return form.get("maxDesync") as AbstractControl<number>;
  }

  getMaxPacketLossControl(form: FormGroup) {
    return form.get("maxPacketLoss") as AbstractControl<number>;
  }

  getEnablePlayerDiagControl(form: FormGroup) {
    return form.get("enablePlayerDiag") as AbstractControl<boolean>;
  }

  getSteamProtocolMaxDataSizeControl(form: FormGroup) {
    return form.get("steamProtocolMaxDataSize") as AbstractControl<number>;
  }

  getMinBandwidth(form: FormGroup) {
    return form.get("minBandwidth") as AbstractControl<number>;
  }

  getMaxBandwidth(form: FormGroup) {
    return form.get("maxBandwidth") as AbstractControl<number>;
  }

  getMaxMsgSend(form: FormGroup) {
    return form.get("maxMsgSend") as AbstractControl<number>;
  }

  getMaxSizeGuaranteed(form: FormGroup) {
    return form.get("maxSizeGuaranteed") as AbstractControl<number>;
  }

  getMaxSizeNonGuaranteed(form: FormGroup) {
    return form.get("maxSizeNonGuaranteed") as AbstractControl<number>;
  }

  getMinErrorToSend(form: FormGroup) {
    return form.get("minErrorToSend") as AbstractControl<number>;
  }

  getMinErrorToSendNear(form: FormGroup) {
    return form.get("minErrorToSendNear") as AbstractControl<number>;
  }

  getMaxCustomFileSize(form: FormGroup) {
    return form.get("maxCustomFileSize") as AbstractControl<number>;
  }

  getMaxPacketSize(form: FormGroup) {
    return form.get("maxPacketSize") as AbstractControl<number>;
  }

  getManualKickTimeout(form: FormGroup) {
    return form.get("manualKickTimeout") as AbstractControl<number>;
  }

  getConnectivityKickTimeout(form: FormGroup) {
    return form.get("connectivityKickTimeout") as AbstractControl<number>;
  }

  getBattlEyeKickTimeout(form: FormGroup) {
    return form.get("battlEyeKickTimeout") as AbstractControl<number>;
  }

  getHarmlessKickTimeout(form: FormGroup) {
    return form.get("harmlessKickTimeout") as AbstractControl<number>;
  }
}
