import { Injectable } from "@angular/core";
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from "@angular/common/http";
import { Observable, tap } from "rxjs";
import { AuthService } from "../service/auth.service";
import { API_BASE_URL } from "../../environments/environment";
import { ApiErrorResponse } from "../api/api-error.model";
import { ApiErrorHandlerService } from "../api/api-error-handler.service";

@Injectable()
export class AswgHttpInterceptor implements HttpInterceptor {
  constructor(
    private readonly authService: AuthService,
    private readonly apiErrorHandler: ApiErrorHandlerService
  ) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if (request.url.startsWith(API_BASE_URL)) {
      if (this.authService.getUsername() && this.authService.getAuthToken()) {
        request = request.clone({
          headers: request.headers.set("Authorization", "Bearer " + this.authService.getAuthToken())
        });
      }
    }

    return next.handle(request).pipe(
      tap({
        error: (error: HttpErrorResponse) => {
          const apiErrorResponse = error.error as ApiErrorResponse;
          if (apiErrorResponse && apiErrorResponse) {
            this.apiErrorHandler.handleError(apiErrorResponse);
          }
        }
      })
    );
  }
}
