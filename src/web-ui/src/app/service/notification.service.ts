import { Injectable } from "@angular/core";
import { HotToastService, ToastOptions } from "@ngxpert/hot-toast";

@Injectable({
  providedIn: "root"
})
export class NotificationService {
  private notificationDurationInSeconds = 5;

  constructor(private toastService: HotToastService) {
  }

  successNotification(message?: string) {
    this.toastService.success(message, this.getDefaultToastConfig());
  }

  errorNotification(message?: string) {
    this.toastService.error(message, this.getDefaultToastConfig());
  }

  warningNotification(message?: string) {
    this.toastService.warning(message, this.getDefaultToastConfig());
  }

  infoNotification(message?: string) {
    this.toastService.info(message, this.getDefaultToastConfig());
  }

  private getDefaultToastConfig(): ToastOptions<any> {
    return {
      duration: this.getDefaultToastDurationTime(),
    }
  }

  private getDefaultToastDurationTime() {
    return this.notificationDurationInSeconds * 1000;
  }
}
