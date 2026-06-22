import { ChangeDetectionStrategy, Component, OnInit } from "@angular/core";
import { ServerNetworkService } from "@service/server-network.service";
import { LoadingSpinnerMaskService } from "@service/loading-spinner-mask.service";
import { NotificationService } from "@service/notification.service";
import { NetworkFormGroupControls, NetworkFormService } from "./network-form.service";
import { AbstractControl, FormGroup, ReactiveFormsModule } from "@angular/forms";
import { MatError, MatFormField, MatInput, MatLabel } from "@angular/material/input";
import { MatTooltip } from "@angular/material/tooltip";
import { MatOption, MatSelect } from "@angular/material/select";
import { MatButton } from "@angular/material/button";
import { stripToDecimals, stripToDigits } from "@app/util/form/form.utils";

@Component({
  selector: "app-network",
  templateUrl: "./network.component.html",
  imports: [
    ReactiveFormsModule,
    MatFormField,
    MatTooltip,
    MatLabel,
    MatSelect,
    MatOption,
    MatError,
    MatInput,
    MatButton
  ],
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrls: ["./network.component.scss"]
})
export class NetworkComponent implements OnInit {
  form: FormGroup<NetworkFormGroupControls>;

  constructor(
    private readonly maskService: LoadingSpinnerMaskService,
    private readonly notificationService: NotificationService,
    private readonly serverNetworkService: ServerNetworkService,
    private readonly formService: NetworkFormService
  ) {
    this.form = this.formService.getForm();
  }

  ngOnInit(): void {
    this.maskService.show();
    this.serverNetworkService.getServerNetworkProperties().subscribe((response) => {
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
          this.notificationService.successNotification(
            "Network settings have been updated!"
          );
        },
        error: () => {
          this.maskService.hide();
          this.notificationService.errorNotification(
            "Network settings have not been updated!"
          );
        }
      });
    }
  }

  hasFormError(controlName: string, errorName: string): boolean | undefined {
    return this.form.get(controlName)?.hasError(errorName);
  }

  allowDecimals(control: AbstractControl<string>) {
    stripToDecimals(control);
  }

  allowDigits(control: AbstractControl<string | number>) {
    stripToDigits(control);
  }
}
