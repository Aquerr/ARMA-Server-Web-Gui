import { inject } from "@angular/core";
import { HttpErrorResponse, HttpInterceptorFn } from "@angular/common/http";
import { tap } from "rxjs";

import { ApiErrorCode, ApiErrorResponse } from "@app/api/api-error.model";
import { ApiErrorHandlerService } from "@app/api/api-error-handler.service";

function isApiErrorResponse(value: unknown): value is ApiErrorResponse {
  return typeof value === "object" && value !== null && "code" in value;
}

function toGenericApiError(response: HttpErrorResponse): ApiErrorResponse {
  return {
    message: response.message,
    code:
      response.status === 403
        ? ApiErrorCode.ACCESS_DENIED
        : ApiErrorCode.SERVER_ERROR,
    status: response.status
  } satisfies ApiErrorResponse;
}

function toApiError(response: HttpErrorResponse): ApiErrorResponse | null {
  const body: unknown = response.error;

  if (isApiErrorResponse(body)) {
    return body;
  }

  if (!body) {
    return null;
  }

  return toGenericApiError(response);
}

export const apiErrorInterceptor: HttpInterceptorFn = (request, next) => {
  const apiErrorHandler = inject(ApiErrorHandlerService);

  return next(request).pipe(
    tap({
      error: (response: HttpErrorResponse) => {
        const apiError = toApiError(response);

        if (apiError) {
          apiErrorHandler.handleError(apiError);
        }
      }
    })
  );
};
