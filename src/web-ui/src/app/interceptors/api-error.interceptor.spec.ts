import {
  HttpClient,
  HttpErrorResponse,
  HttpHandlerFn,
  HttpRequest,
  HttpResponse,
  provideHttpClient,
  withInterceptors
} from "@angular/common/http";
import { HttpTestingController, provideHttpClientTesting } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { firstValueFrom, of, throwError } from "rxjs";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { ApiErrorCode, ApiErrorResponse } from "@app/api/api-error.model";
import { ApiErrorHandlerService } from "@app/api/api-error-handler.service";

import { apiErrorInterceptor } from "./api-error.interceptor";

describe("apiErrorInterceptor", () => {
  const handleError = vi.fn<(error: ApiErrorResponse) => void>();

  beforeEach(() => {
    handleError.mockReset();

    TestBed.configureTestingModule({
      providers: [
        { provide: ApiErrorHandlerService, useValue: { handleError } },
        provideHttpClient(withInterceptors([apiErrorInterceptor])),
        provideHttpClientTesting()
      ]
    });
  });

  /** Runs the interceptor in an injection context with the given `next` handler. */
  function intercept(next: HttpHandlerFn) {
    const request = new HttpRequest("GET", "/api/test");
    return TestBed.runInInjectionContext(() => apiErrorInterceptor(request, next));
  }

  function failWith(error: HttpErrorResponse): HttpHandlerFn {
    return () => throwError(() => error);
  }

  function httpError(init: ConstructorParameters<typeof HttpErrorResponse>[0]) {
    return new HttpErrorResponse({ url: "/api/test", statusText: "Error", ...init });
  }

  describe("successful responses", () => {
    it("passes the response through and does not call the error handler", async () => {
      const response = new HttpResponse({ status: 200, body: { ok: true } });

      await expect(firstValueFrom(intercept(() => of(response)))).resolves.toBe(response);

      expect(handleError).not.toHaveBeenCalled();
    });
  });

  describe("errors with an API error body", () => {
    it("forwards the body to the error handler", async () => {
      const body = {
        message: "Validation failed",
        code: ApiErrorCode.SERVER_ERROR,
        status: 400
      } satisfies ApiErrorResponse;
      const error = httpError({ error: body, status: 400 });

      await expect(firstValueFrom(intercept(failWith(error)))).rejects.toBe(error);

      expect(handleError).toHaveBeenCalledTimes(1);
      expect(handleError).toHaveBeenCalledWith(body);
    });

    it("still propagates the original HttpErrorResponse to the subscriber", async () => {
      const error = httpError({
        error: { message: "Nope", code: ApiErrorCode.ACCESS_DENIED, status: 403 },
        status: 403
      });

      await expect(firstValueFrom(intercept(failWith(error)))).rejects.toBe(error);
    });

    it.each([null, undefined, ""])(
      "does not call the error handler when the error body is %j",
      async (body) => {
        const error = httpError({ error: body, status: 500 });

        await expect(firstValueFrom(intercept(failWith(error)))).rejects.toBe(error);

        expect(handleError).not.toHaveBeenCalled();
      }
    );
  });

  describe("errors with a Blob body", () => {
    it("maps a 403 to ACCESS_DENIED", async () => {
      const error = httpError({ error: new Blob(["denied"]), status: 403, statusText: "Forbidden" });

      await expect(firstValueFrom(intercept(failWith(error)))).rejects.toBe(error);

      expect(handleError).toHaveBeenCalledTimes(1);
      expect(handleError).toHaveBeenCalledWith({
        message: error.message,
        code: ApiErrorCode.ACCESS_DENIED,
        status: 403
      } satisfies ApiErrorResponse);
    });

    it.each([400, 401, 404, 500, 503])(
      "maps a %i to SERVER_ERROR",
      async (status) => {
        const error = httpError({ error: new Blob(["boom"]), status });

        await expect(firstValueFrom(intercept(failWith(error)))).rejects.toBe(error);

        expect(handleError).toHaveBeenCalledTimes(1);
        expect(handleError).toHaveBeenCalledWith({
          message: error.message,
          code: ApiErrorCode.SERVER_ERROR,
          status
        } satisfies ApiErrorResponse);
      }
    );

    it("calls the error handler even when the Blob is empty", async () => {
      const error = httpError({ error: new Blob([]), status: 500 });

      await expect(firstValueFrom(intercept(failWith(error)))).rejects.toBe(error);

      expect(handleError).toHaveBeenCalledTimes(1);
    });
  });

  describe("errors with a body that is not an API error", () => {
    it("reports a plain-text body (e.g. a proxy error page) as SERVER_ERROR", async () => {
      const error = httpError({ error: "<html>502 Bad Gateway</html>", status: 502 });

      await expect(firstValueFrom(intercept(failWith(error)))).rejects.toBe(error);

      expect(handleError).toHaveBeenCalledTimes(1);
      expect(handleError).toHaveBeenCalledWith({
        message: error.message,
        code: ApiErrorCode.SERVER_ERROR,
        status: 502
      } satisfies ApiErrorResponse);
    });

    it("maps a 403 with a plain-text body to ACCESS_DENIED", async () => {
      const error = httpError({ error: "Forbidden", status: 403 });

      await expect(firstValueFrom(intercept(failWith(error)))).rejects.toBe(error);

      expect(handleError).toHaveBeenCalledTimes(1);
      expect(handleError).toHaveBeenCalledWith({
        message: error.message,
        code: ApiErrorCode.ACCESS_DENIED,
        status: 403
      } satisfies ApiErrorResponse);
    });

    it("reports a network failure (ProgressEvent body) as SERVER_ERROR with status 0", async () => {
      const error = httpError({ error: new ProgressEvent("error"), status: 0 });

      await expect(firstValueFrom(intercept(failWith(error)))).rejects.toBe(error);

      expect(handleError).toHaveBeenCalledTimes(1);
      expect(handleError).toHaveBeenCalledWith({
        message: error.message,
        code: ApiErrorCode.SERVER_ERROR,
        status: 0
      } satisfies ApiErrorResponse);
    });

    it("does not forward an object body without an error code as-is", async () => {
      const body = { detail: "something unexpected" };
      const error = httpError({ error: body, status: 500 });

      await expect(firstValueFrom(intercept(failWith(error)))).rejects.toBe(error);

      expect(handleError).toHaveBeenCalledTimes(1);
      expect(handleError).not.toHaveBeenCalledWith(body);
      expect(handleError).toHaveBeenCalledWith({
        message: error.message,
        code: ApiErrorCode.SERVER_ERROR,
        status: 500
      } satisfies ApiErrorResponse);
    });
  });

  describe("integration with HttpClient", () => {
    it("reports a JSON error body sent by the API", () => {
      const http = TestBed.inject(HttpClient);
      const httpTesting = TestBed.inject(HttpTestingController);
      const body = {
        message: "Something broke",
        code: ApiErrorCode.SERVER_ERROR,
        status: 500
      } satisfies ApiErrorResponse;

      http.get("/api/test").subscribe({
        error: () => { /* empty */
        }
      });

      httpTesting
        .expectOne("/api/test")
        .flush(body, { status: 500, statusText: "Internal Server Error" });

      expect(handleError).toHaveBeenCalledTimes(1);
      expect(handleError).toHaveBeenCalledWith(body);
      httpTesting.verify();
    });

    it("reports a failed blob download as ACCESS_DENIED on 403", () => {
      const http = TestBed.inject(HttpClient);
      const httpTesting = TestBed.inject(HttpTestingController);

      http.get("/api/download", { responseType: "blob" }).subscribe({
        error: () => { /* empty */
        }
      });

      httpTesting
        .expectOne("/api/download")
        .flush(new Blob(["denied"]), { status: 403, statusText: "Forbidden" });

      expect(handleError).toHaveBeenCalledTimes(1);
      expect(handleError).toHaveBeenCalledWith(
        expect.objectContaining({ code: ApiErrorCode.ACCESS_DENIED, status: 403 })
      );
      httpTesting.verify();
    });

    it("reports a network failure as a generic SERVER_ERROR", () => {
      const http = TestBed.inject(HttpClient);
      const httpTesting = TestBed.inject(HttpTestingController);

      http.get("/api/test").subscribe({
        error: () => { /* empty */
        }
      });

      httpTesting.expectOne("/api/test").error(new ProgressEvent("error"));

      expect(handleError).toHaveBeenCalledTimes(1);
      expect(handleError).toHaveBeenCalledWith(
        expect.objectContaining({ code: ApiErrorCode.SERVER_ERROR, status: 0 })
      );
      httpTesting.verify();
    });
  });
});
