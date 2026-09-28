import {
  HttpClient,
  HttpHandlerFn,
  HttpHeaders,
  HttpParams,
  HttpRequest,
  HttpResponse,
  provideHttpClient,
  withInterceptors
} from "@angular/common/http";
import { HttpTestingController, provideHttpClientTesting } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { of } from "rxjs";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { API_BASE_URL } from "@environments/environment";
import { AuthService } from "@service/auth.service";

import { authInterceptor } from "./auth.interceptor";

describe("authInterceptor", () => {
  const authToken = vi.fn<() => string | null | undefined>();

  beforeEach(() => {
    authToken.mockReset();
    authToken.mockReturnValue(null);

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: { authToken } },
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting()
      ]
    });
  });

  function intercept(request: HttpRequest<unknown>) {
    const next = vi.fn<HttpHandlerFn>(() => of(new HttpResponse({ status: 200 })));
    TestBed.runInInjectionContext(() => authInterceptor(request, next));
    return next;
  }

  function forwardedRequest(next: ReturnType<typeof intercept>): HttpRequest<unknown> {
    expect(next).toHaveBeenCalledTimes(1);
    return next.mock.calls[0][0];
  }

  describe("requests to the API", () => {
    it("adds a Bearer Authorization header when a token is available", () => {
      authToken.mockReturnValue("abc123");
      const request = new HttpRequest("GET", `${API_BASE_URL}/users`);

      const forwarded = forwardedRequest(intercept(request));

      expect(forwarded.headers.get("Authorization")).toBe("Bearer abc123");
    });

    it("overrides an Authorization header that is already present", () => {
      authToken.mockReturnValue("fresh-token");
      const request = new HttpRequest("GET", `${API_BASE_URL}/users`, {
        headers: new HttpHeaders({ Authorization: "Bearer stale-token" })
      });

      const forwarded = forwardedRequest(intercept(request));

      expect(forwarded.headers.getAll("Authorization")).toEqual(["Bearer fresh-token"]);
    });

    it("preserves the method, url, body, params and other headers", () => {
      authToken.mockReturnValue("abc123");
      const body = { name: "Nerdi" };
      const request = new HttpRequest("POST", `${API_BASE_URL}/users`, body, {
        headers: new HttpHeaders({ "X-Custom": "custom-value" }),
        params: new HttpParams().set("page", "2")
      });

      const forwarded = forwardedRequest(intercept(request));

      expect(forwarded.method).toBe("POST");
      expect(forwarded.url).toBe(`${API_BASE_URL}/users`);
      expect(forwarded.body).toEqual(body);
      expect(forwarded.params.get("page")).toBe("2");
      expect(forwarded.headers.get("X-Custom")).toBe("custom-value");
      expect(forwarded.headers.get("Authorization")).toBe("Bearer abc123");
    });

    it("does not mutate the original request", () => {
      authToken.mockReturnValue("abc123");
      const request = new HttpRequest("GET", `${API_BASE_URL}/users`);

      const forwarded = forwardedRequest(intercept(request));

      expect(forwarded).not.toBe(request);
      expect(request.headers.has("Authorization")).toBe(false);
    });

    it.each([null, undefined, ""])(
      "forwards the original request untouched when the token is %j",
      (token) => {
        authToken.mockReturnValue(token);
        const request = new HttpRequest("GET", `${API_BASE_URL}/users`);

        const forwarded = forwardedRequest(intercept(request));

        expect(forwarded).toBe(request);
        expect(forwarded.headers.has("Authorization")).toBe(false);
      }
    );
  });

  describe("requests outside the API", () => {
    it("forwards the original request untouched, even if a token exists", () => {
      authToken.mockReturnValue("abc123");
      const request = new HttpRequest("GET", "https://third-party.example.com/data");

      const forwarded = forwardedRequest(intercept(request));

      expect(forwarded).toBe(request);
      expect(forwarded.headers.has("Authorization")).toBe(false);
    });

    it("does not read the token at all", () => {
      authToken.mockReturnValue("abc123");

      intercept(new HttpRequest("GET", "https://third-party.example.com/data"));

      expect(authToken).not.toHaveBeenCalled();
    });
  });

  describe("API base URL matching", () => {
    const apiBase = API_BASE_URL.replace(/\/+$/, "");

    it.each(["", "/", "/users", "?page=1", "/users?page=1#top"])(
      "treats %j after the base URL as an API request",
      (suffix) => {
        authToken.mockReturnValue("abc123");
        const request = new HttpRequest("GET", `${apiBase}${suffix}`);

        const forwarded = forwardedRequest(intercept(request));

        expect(forwarded.headers.get("Authorization")).toBe("Bearer abc123");
      }
    );

    it.each([".evil.example.com/steal", "-other/users", "@evil.example.com/steal", ":8443/users"])(
      "does not leak the token to a lookalike URL with %j appended to the base",
      (suffix) => {
        authToken.mockReturnValue("abc123");
        const request = new HttpRequest("GET", `${apiBase}${suffix}`);

        const forwarded = forwardedRequest(intercept(request));

        expect(forwarded).toBe(request);
        expect(forwarded.headers.has("Authorization")).toBe(false);
        expect(authToken).not.toHaveBeenCalled();
      }
    );
  });

  describe("integration with HttpClient", () => {
    it("attaches the token to API calls made through HttpClient", () => {
      authToken.mockReturnValue("abc123");
      const http = TestBed.inject(HttpClient);
      const httpTesting = TestBed.inject(HttpTestingController);

      http.get(`${API_BASE_URL}/users`).subscribe();

      const testRequest = httpTesting.expectOne(`${API_BASE_URL}/users`);
      expect(testRequest.request.headers.get("Authorization")).toBe("Bearer abc123");
      testRequest.flush([]);
      httpTesting.verify();
    });

    it("leaves external calls made through HttpClient without a token", () => {
      authToken.mockReturnValue("abc123");
      const http = TestBed.inject(HttpClient);
      const httpTesting = TestBed.inject(HttpTestingController);

      http.get("https://third-party.example.com/data").subscribe();

      const testRequest = httpTesting.expectOne("https://third-party.example.com/data");
      expect(testRequest.request.headers.has("Authorization")).toBe(false);
      testRequest.flush({});
      httpTesting.verify();
    });
  });
});
