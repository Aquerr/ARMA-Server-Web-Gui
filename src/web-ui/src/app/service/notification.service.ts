import { Injectable } from "@angular/core";
import { ToastrService } from "ngx-toastr";

@Injectable({
  providedIn: "root"
})
export class NotificationService {
  private notificationDurationInSeconds = 5;

  constructor(private toastrService: ToastrService) {}

  successNotification(message?: string, title?: string | "Success") {
    this.toastrService.success(message, title, {
      timeOut: this.notificationDurationInSeconds * 1000,
      positionClass: "toast-top-right"
    });
  }

  errorNotification(message?: string, title?: string | "Error") {
    this.toastrService.error(message, title, {
      timeOut: this.notificationDurationInSeconds * 1000,
      positionClass: "toast-top-right"
    });
  }

  warningNotification(message?: string, title?: string | "Warning") {
    this.toastrService.warning(message, title, {
      timeOut: this.notificationDurationInSeconds * 1000,
      positionClass: "toast-top-right"
    });
  }

  infoNotification(message?: string, title?: string | "Information") {
    this.toastrService.info(message, title, {
      timeOut: this.notificationDurationInSeconds * 1000,
      positionClass: "toast-top-right"
    });
  }
}
