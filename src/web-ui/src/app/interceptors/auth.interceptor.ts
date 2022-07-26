import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor, HttpErrorResponse
} from '@angular/common/http';
import {Observable, tap} from 'rxjs';
import {Router} from "@angular/router";
import {AuthService} from "../service/auth.service";
import {MaskService} from "../service/mask.service";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private router: Router, private authService: AuthService, private maskService: MaskService) {}

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
        } else {
          console.warn(error.message);
        }
      }
    }));
  }
}
