import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor, HttpErrorResponse
} from '@angular/common/http';
import {Observable, tap} from 'rxjs';
import {Router} from "@angular/router";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private router: Router) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(tap(() => {
        if (sessionStorage.getItem('username') && sessionStorage.getItem('auth-token')) {
          request = request.clone({
            headers: request.headers.set('Authorization', 'Bearer ' + sessionStorage.getItem('auth-token'))
          });
        }
      },
    (error: any) => {
          if (error instanceof HttpErrorResponse) {
            if (error.status === 401) {
              console.warn("No access! Provide auth token!");
            }
          }
          this.router.navigate(['login']);
    }))
  }
}
