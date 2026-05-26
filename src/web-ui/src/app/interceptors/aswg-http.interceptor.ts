import { Injectable } from "@angular/core";
import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Observable, tap } from "rxjs";
import { AuthService } from "../service/auth.service";
import { API_BASE_URL } from "../../environments/environment";
import { ApiErrorCode, ApiErrorResponse } from "../api/api-error.model";
import { ApiErrorHandlerService } from "../api/api-error-handler.service";

@Injectable()
export class AswgHttpInterceptor implements HttpInterceptor {
  constructor(
    private readonly authService: AuthService,
    private readonly apiErrorHandler: ApiErrorHandlerService
  ) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if (request.url.startsWith(API_BASE_URL)) {
      if (this.authService.getAuthToken()) {
        request = request.clone({
          headers: request.headers.set("Authorization", "Bearer " + this.authService.getAuthToken())
        });
      }
    }

    return next.handle(request).pipe(
      tap({
        error: (response: HttpErrorResponse) => {
          if (response.error instanceof Blob) {
            this.apiErrorHandler.handleError({
              message: response.message,
              code: response.status === 403 ? ApiErrorCode.ACCESS_DENIED : ApiErrorCode.SERVER_ERROR,
              status: response.status
            } satisfies ApiErrorResponse);
            return;
          }

          const apiErrorResponse = response.error as ApiErrorResponse;
          if (apiErrorResponse) {
            this.apiErrorHandler.handleError(apiErrorResponse);
          }
        }
      })
    );
  }
}
