import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor, HttpErrorResponse } from '@angular/common/http';
import {Observable, tap} from 'rxjs';
import {Router} from "@angular/router";
import {AuthService} from "../service/auth.service";
import {MaskService} from "../service/mask.service";
import {NotificationService} from "../service/notification.service";
import {API_BASE_URL} from "../../environments/environment";

@Injectable()
export class AswgHttpInterceptor implements HttpInterceptor {

  constructor(private readonly router: Router,
              private readonly authService: AuthService,
              private readonly maskService: MaskService,
              private readonly notificationService: NotificationService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    if (request.url.startsWith(API_BASE_URL)) {
      if (this.authService.getUsername() && this.authService.getAuthToken()) {
        request = request.clone({
          headers: request.headers.set('Authorization', 'Bearer ' + this.authService.getAuthToken())
        });
      }
    }

    return next.handle(request).pipe(tap({
      error: (error: HttpErrorResponse) => {
        const errorResponse = error.error;
        if (error.status === 401) {
          if (this.authService.isAuthenticated()) {
            this.authService.clearAuth();
          }
          this.router.navigate(['login']);
          this.maskService.hide();
          const errorMessage = errorResponse.message || "You are not authorized.";
          this.notificationService.warningNotification(errorMessage, "Unauthorized");
        } else if (error.status === 404) {
          const errorMessage = errorResponse.message || "Resource has not been found.";
          this.router.navigate(['']);
          this.maskService.hide();
          this.notificationService.errorNotification(errorMessage, "Not found");
        } else if (error.status === 500) {
          const errorMessage = errorResponse.message || "An error occurred on the server.";
          this.maskService.hide();
          this.notificationService.errorNotification(errorMessage, "Server error");
        } else {
          console.warn(error.message);
        }
      }
    }));
  }
}
