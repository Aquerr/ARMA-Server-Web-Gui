import { inject, Injectable } from "@angular/core";
import { ApiErrorCode, ApiErrorResponse } from "./api-error.model";
import { AuthService } from "../service/auth.service";
import { Router } from "@angular/router";
import { NotificationService } from "../service/notification.service";
import { MaskService } from "../service/mask.service";

@Injectable({
  providedIn: "root"
})
export class ApiErrorHandlerService {
  private readonly errorHandlers: Map<ApiErrorCode, Function> = new Map<ApiErrorCode, () => void>([
    [ApiErrorCode.AUTH_TOKEN_EXPIRED, this.handleAuthTokenExpired],
    [ApiErrorCode.BAD_AUTH_TOKEN, this.handleAuthTokenExpired],
    [ApiErrorCode.AUTH_TOKEN_MISSING, this.handleAuthTokenMissing]
  ]);

  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly notificationService = inject(NotificationService);
  private readonly maskService = inject(MaskService);

  public handleError(apiErrorResponse: ApiErrorResponse) {
    this.maskService.hide();

    const handler = this.errorHandlers.get(apiErrorResponse.code);
    if (handler) {
      handler.call(this);
      return;
    }

    if (apiErrorResponse.status === 401) {
      const errorMessage = apiErrorResponse.message || "You are not authorized.";
      this.notificationService.warningNotification(errorMessage, "Unauthorized");
    } else if (apiErrorResponse.status === 404) {
      const errorMessage = apiErrorResponse.message || "Resource has not been found.";
      this.notificationService.errorNotification(errorMessage, "Not found");
      this.router.navigate([""]);
    } else {
      const errorMessage = apiErrorResponse.message || "An error occurred on the server.";
      this.notificationService.errorNotification(errorMessage, "Server error");
    }
  }

  private handleAuthTokenExpired(): void {
    this.authService.clearAuth();
    this.router.navigateByUrl("/login");
    this.notificationService.warningNotification("Session expired. Please log in again.");
  }

  private handleAuthTokenMissing() {
    this.router.navigateByUrl("/login");
    this.notificationService.warningNotification("You are not authenticated. Please log in.");
  }
}
