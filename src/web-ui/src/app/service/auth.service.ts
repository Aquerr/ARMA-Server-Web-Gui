import { computed, inject, Injectable, signal } from "@angular/core";
import { map, Observable, tap } from "rxjs";
import { HttpClient, HttpResponse } from "@angular/common/http";
import { API_BASE_URL } from "@environments/environment";
import { JwtTokenResponse } from "@model/jwt.model";
import { AswgAuthority } from "@model/authority.model";

@Injectable({
  providedIn: "root"
})
export class AuthService {
  private static readonly STORAGE_USERNAME_KEY = "username";
  private static readonly STORAGE_AUTH_TOKEN_KEY = "auth-token";
  private static readonly STORAGE_AUTHORITIES_KEY = "authorities";

  private readonly httpClient = inject(HttpClient);

  private readonly usernameSignal = signal<string | null>(
    sessionStorage.getItem(AuthService.STORAGE_USERNAME_KEY)
  );

  private readonly authTokenSignal = signal<string | null>(
    sessionStorage.getItem(AuthService.STORAGE_AUTH_TOKEN_KEY)
  );

  private readonly authoritiesSignal = signal<AswgAuthority[]>(
    AuthService.readAuthoritiesFromStorage()
  );

  readonly username = this.usernameSignal.asReadonly();
  readonly authorities = this.authoritiesSignal.asReadonly();
  readonly authToken = this.authTokenSignal.asReadonly();
  readonly isAuthenticated = computed(
    () => this.usernameSignal() != null && this.authTokenSignal() != null
  );

  authenticate(username: string, password: string): Observable<HttpResponse<JwtTokenResponse>> {
    return this.httpClient
      .post<JwtTokenResponse>(
        API_BASE_URL + "/auth",
        { username, password },
        { observe: "response" }
      )
      .pipe(
        map((userData) => {
          const token = userData.body?.jwt ?? "";
          const authorities = userData.body?.authorities ?? [];

          sessionStorage.setItem(AuthService.STORAGE_USERNAME_KEY, username);
          sessionStorage.setItem(AuthService.STORAGE_AUTH_TOKEN_KEY, token);
          sessionStorage.setItem(AuthService.STORAGE_AUTHORITIES_KEY, JSON.stringify(authorities));

          this.usernameSignal.set(username);
          this.authTokenSignal.set(token);
          this.authoritiesSignal.set(authorities as AswgAuthority[]);

          return userData;
        })
      );
  }

  clearAuth(): void {
    sessionStorage.removeItem(AuthService.STORAGE_USERNAME_KEY);
    sessionStorage.removeItem(AuthService.STORAGE_AUTH_TOKEN_KEY);
    sessionStorage.removeItem(AuthService.STORAGE_AUTHORITIES_KEY);

    this.usernameSignal.set(null);
    this.authTokenSignal.set(null);
    this.authoritiesSignal.set([]);
  }

  logout(): Observable<void> {
    return this.httpClient
      .post<void>(`${API_BASE_URL}/auth/logout`, null)
      .pipe(tap(() => this.clearAuth()));
  }

  private static readAuthoritiesFromStorage(): AswgAuthority[] {
    const authoritiesString = sessionStorage.getItem(AuthService.STORAGE_AUTHORITIES_KEY);
    if (!authoritiesString) return [];
    return (JSON.parse(authoritiesString) as string[]).map((a) => a as AswgAuthority);
  }
}
