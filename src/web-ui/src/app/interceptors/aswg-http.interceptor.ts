import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor, HttpErrorResponse } from '@angular/common/http';
import {Observable, tap} from 'rxjs';
import {Router} from "@angular/router";
import {AuthService} from "../service/auth.service";
import {MaskService} from "../service/mask.service";
import {NotificationService} from "../service/notification.service";

@Injectable()
export class AswgHttpInterceptor implements HttpInterceptor {

  constructor(private router: Router,
              private authService: AuthService,
              private maskService: MaskService,
              private notificationService: NotificationService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (sessionStorage.getItem('username') && sessionStorage.getItem('auth-token')) {
      request = request.clone({
        headers: request.headers.set('Authorization', 'Bearer ' + sessionStorage.getItem('auth-token'))
      });
    }

    return next.handle(request).pipe(tap(
      () => {},
      error => {
      if (error instanceof HttpErrorResponse) {
        if (error.status === 401) {
          this.authService.logout();
          this.router.navigate(['login']);
          this.maskService.hide();
          this.notificationService.warningNotification("You are not authorized.", "Unauthorized");
        } else if (error.status === 404) {
          this.router.navigate(['']);
          this.maskService.hide();
          this.notificationService.errorNotification("Resource has not been found.", "Not found");
        } else if (error.status === 500) {
          this.maskService.hide();
          this.notificationService.errorNotification("An error occurred on the server.", "Server error");
        } else {
          console.warn(error.message);
        }
      }
    }));
  }
}
