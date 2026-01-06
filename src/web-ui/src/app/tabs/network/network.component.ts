import { Component, OnInit } from "@angular/core";
import { ServerNetworkService } from "../../service/server-network.service";
import { LoadingSpinnerMaskService } from "../../service/loading-spinner-mask.service";
import { NotificationService } from "../../service/notification.service";
import { NetworkFormService } from "./network-form.service";
import { FormGroup } from "@angular/forms";

@Component({
  selector: "app-network",
  templateUrl: "./network.component.html",
  styleUrls: ["./network.component.scss"],
  standalone: false
})
export class NetworkComponent implements OnInit {
  form: FormGroup;

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
            "Network settings have been updated!",
            "Success"
          );
        },
        error: () => {
          this.maskService.hide();
          this.notificationService.errorNotification(
            "Network settings have not been updated!",
            "Error"
          );
        }
      });
    }
  }

  hasFormError(controlName: string, errorName: string): boolean | undefined {
    return this.form.get(controlName)?.hasError(errorName);
  }

  allowDecimals(event: KeyboardEvent) {
    const target = event.target as HTMLInputElement;
    target.value = target.value.replace(/[^.\d]/, "");
    this.form.markAllAsTouched();
  }

  allowDigits(event: KeyboardEvent) {
    const target = event.target as HTMLInputElement;
    target.value = target.value.replace(/\D+/, "");
    this.form.markAllAsTouched();
  }
}
