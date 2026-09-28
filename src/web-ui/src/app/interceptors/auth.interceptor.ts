import { inject } from "@angular/core";
import { HttpInterceptorFn } from "@angular/common/http";

import { API_BASE_URL } from "@environments/environment";
import { AuthService } from "@service/auth.service";

const API_BASE = API_BASE_URL.replace(/\/+$/, "");

function isApiRequest(url: string): boolean {
  if (!url.startsWith(API_BASE)) {
    return false;
  }

  const boundary = url.charAt(API_BASE.length);

  return boundary === "" || boundary === "/" || boundary === "?" || boundary === "#";
}

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  if (!isApiRequest(request.url)) {
    return next(request);
  }

  const authToken = inject(AuthService).authToken();

  if (!authToken) {
    return next(request);
  }

  const authenticatedRequest = request.clone({
    headers: request.headers.set(
      "Authorization",
      `Bearer ${authToken}`
    )
  });

  return next(authenticatedRequest);
};
